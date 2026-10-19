package ink.yunduan.nest.web;

import ink.yunduan.nest.domain.Article;
import ink.yunduan.nest.repo.ArticleRepository;
import ink.yunduan.nest.service.AuthService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleRepository articleRepository;
    private final AuthService authService;

    public ArticleController(ArticleRepository articleRepository, AuthService authService) {
        this.articleRepository = articleRepository;
        this.authService = authService;
    }

    @GetMapping
    public List<Article> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String q
    ) {
        String wantedCategory = category == null ? "" : category.trim();
        String wantedTag = tag == null ? "" : tag.trim();
        String keyword = q == null ? "" : q.trim().toLowerCase(Locale.ROOT);

        return articleRepository.findAllByOrderByPublishedAtDesc().stream()
                .filter(Article::isPublished)
                .filter(article -> wantedCategory.isEmpty() || article.getCategory().equals(wantedCategory))
                .filter(article -> wantedTag.isEmpty() || article.getTags().contains(wantedTag))
                .filter(article -> {
                    if (keyword.isEmpty()) {
                        return true;
                    }
                    String haystack = (article.getTitle() + " " + article.getExcerpt() + " "
                            + String.join(" ", article.getTags()) + " " + article.getCategory())
                            .toLowerCase(Locale.ROOT);
                    return haystack.contains(keyword);
                })
                .toList();
    }

    @GetMapping("/{slug}")
    @Transactional
    public Article detail(
            @PathVariable String slug,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new NoSuchElementException("这篇文章可能被风吹走了。"));

        if (!article.isPublished() && !authService.isAdmin(authService.resolve(authorization))) {
            throw new NoSuchElementException("这篇文章还没有发布。");
        }

        if (article.isPublished()) {
            article.setViews(article.getViews() + 1);
            article = articleRepository.save(article);
        }
        return article;
    }
}
