package ink.yunduan.nest.web.dto;

import java.util.List;

public record StatsResponse(
        int articles,
        int tags,
        int categories,
        int words,
        int todayVisits,
        int monthVisits,
        int totalVisits,
        int todayViews,
        int monthViews,
        int totalViews,
        List<VisitPoint> visitTrend,
        List<MonthPoint> monthlyArticles
) {
    public record VisitPoint(String date, int pv, int uv) {
    }

    public record MonthPoint(String month, int count) {
    }
}
