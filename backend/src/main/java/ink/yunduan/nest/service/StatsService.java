package ink.yunduan.nest.service;

import ink.yunduan.nest.domain.Article;
import ink.yunduan.nest.domain.VisitDaily;
import ink.yunduan.nest.repo.ArticleRepository;
import ink.yunduan.nest.repo.VisitDailyRepository;
import ink.yunduan.nest.web.dto.StatsResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private static final DateTimeFormatter MONTH_LABEL = DateTimeFormatter.ofPattern("yy/MM");

    private final ArticleRepository articleRepository;
    private final VisitDailyRepository visitRepository;
    private final Map<LocalDate, Set<String>> seenVisitors = new ConcurrentHashMap<>();

    public StatsService(ArticleRepository articleRepository, VisitDailyRepository visitRepository) {
        this.articleRepository = articleRepository;
        this.visitRepository = visitRepository;
    }

    @Transactional(readOnly = true)
    public StatsResponse build() {
        List<Article> articles = articleRepository.findAllByOrderByPublishedAtDesc().stream()
                .filter(Article::isPublished)
                .toList();
        Set<String> tags = articles.stream()
                .flatMap(article -> article.getTags().stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Set<String> categories = articles.stream()
                .map(Article::getCategory)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        int words = articles.stream().mapToInt(Article::getWords).sum();

        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        List<VisitDaily> allVisits = visitRepository.findAllByOrderByDateAsc();
        Map<LocalDate, VisitDaily> byDate = allVisits.stream()
                .collect(Collectors.toMap(VisitDaily::getDate, visit -> visit, (a, b) -> b));

        VisitDaily todayVisit = byDate.getOrDefault(today, new VisitDaily());
        int monthVisits = allVisits.stream()
                .filter(visit -> !visit.getDate().isBefore(monthStart))
                .mapToInt(VisitDaily::getUv)
                .sum();
        int monthViews = allVisits.stream()
                .filter(visit -> !visit.getDate().isBefore(monthStart))
                .mapToInt(VisitDaily::getPv)
                .sum();
        int totalVisits = allVisits.stream().mapToInt(VisitDaily::getUv).sum();
        int totalViews = allVisits.stream().mapToInt(VisitDaily::getPv).sum();

        List<StatsResponse.VisitPoint> trend = new ArrayList<>();
        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            VisitDaily visit = byDate.get(date);
            trend.add(new StatsResponse.VisitPoint(
                    date.toString(),
                    visit == null ? 0 : visit.getPv(),
                    visit == null ? 0 : visit.getUv()
            ));
        }

        Map<YearMonth, Long> articleMonths = articles.stream()
                .collect(Collectors.groupingBy(article -> YearMonth.from(article.getPublishedAt()), Collectors.counting()));
        List<StatsResponse.MonthPoint> monthly = new ArrayList<>();
        YearMonth current = YearMonth.from(today);
        for (int i = 9; i >= 0; i--) {
            YearMonth month = current.minusMonths(i);
            monthly.add(new StatsResponse.MonthPoint(
                    month.format(MONTH_LABEL),
                    articleMonths.getOrDefault(month, 0L).intValue()
            ));
        }

        return new StatsResponse(
                articles.size(),
                tags.size(),
                categories.size(),
                words,
                todayVisit.getUv(),
                monthVisits,
                totalVisits,
                todayVisit.getPv(),
                monthViews,
                totalViews,
                trend,
                monthly
        );
    }

    @Transactional
    public void track(HttpServletRequest request) {
        LocalDate today = LocalDate.now();
        VisitDaily visit = visitRepository.findByDate(today).orElseGet(() -> {
            VisitDaily fresh = new VisitDaily();
            fresh.setDate(today);
            fresh.setPv(0);
            fresh.setUv(0);
            return fresh;
        });

        visit.setPv(visit.getPv() + 1);
        String ip = clientIp(request);
        boolean firstToday = seenVisitors
                .computeIfAbsent(today, key -> ConcurrentHashMap.newKeySet())
                .add(ip);
        if (firstToday) {
            visit.setUv(visit.getUv() + 1);
        }
        visitRepository.save(visit);

        seenVisitors.keySet().removeIf(date -> date.isBefore(today.minusDays(7)));
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        String remote = request.getRemoteAddr();
        return remote == null ? "unknown" : remote;
    }
}
