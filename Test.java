import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Test {


    public static final String PDF = ".pdf";

    public static void main(String... args) {

        Instant aWeekAgo = Instant.now().minus(Duration.ofDays(8));
        Map<String, AtomicInteger> analytics = groupByResourceAndUser(aWeekAgo, loadReadership());
        //ResourceUtil.notFoundIfEmpty(analytics, "Readership not found");

        List<ReadershipAnalytics> readershipForUser = readershipForUser(analytics, "rp");
        //ResourceUtil.notFoundIfEmpty(readershipForUser, "Readership not found");

        readershipForUser.sort(Comparator.comparing(ReadershipAnalytics::getReadCount).reversed());

    }

    //dummy
    private static List<Readership> loadReadership() {

        List<Readership> readerships = new ArrayList<>();
        readerships.add(new Readership("1", "12345", "111.pdf", Instant.now(), "rp"));
        readerships.add(new Readership("2", "12346", "112.pdf", Instant.now(), "rp"));
        readerships.add(new Readership("3", "12347", "113.pdf", Instant.now(), "rp"));
        readerships.add(new Readership("4", "12348", "113.pdf", Instant.now(), "rp"));
        readerships.add(new Readership("5", "12349", "115.pdf", Instant.now(), "rp"));
        readerships.add(new Readership("6", "12341", "116.pdf", Instant.now(), "rp"));
        readerships.add(new Readership("7", "12342", "117.pdf", Instant.now(), "rp"));


        return readerships;
    }

    private static Map<String, AtomicInteger> groupByResourceAndUser(Instant startTime, Collection<Readership> readerships) {
        Map<String, AtomicInteger> reportMap = new HashMap<>();
        readerships.parallelStream()
                .filter(r -> r.getCreatedAt().isAfter(startTime))
                .collect(Collectors.toMap(r -> r.getResourceId() + "-" + r.getUserId(), Function.identity(), (r1, r2) -> r1))
                .forEach((k, v) -> {
                            reportMap.putIfAbsent(v.getResourceId(), new AtomicInteger(0));
                            reportMap.get(v.getResourceId()).getAndIncrement();
                        }
                );

        //return reportMap.keySet().stream().map(k -> new ReadershipAnalytics(k, k.replace(PDF, ""), reportMap.get(k).intValue())).collect(Collectors.toList());
        return reportMap;
    }


    private static List<ReadershipAnalytics> readershipForUser(Map<String, AtomicInteger> analytics, String applicationId) {
       /* List<ResourceIdentifier> identifiers = analytics.keySet().stream().map(k -> ResourceIdentifier.builder()
                .resourceId(k)
                .applicationId(applicationId)
                .classification("Report") //TODO this param should be passed from UI
                .build()).collect(Collectors.toList());*/

        //Map<String, ResourceSummary> summaryMap = contentCacheService.resourceSummariesByClassificationAndResourceId(identifiers, contentCacheContext);
        Map<String, ResourceSummary> summaryMap = getResourceSummery();
        //return analytics.stream().filter(a -> (summaryMap.get(a.getResourceId()) != null)).map(a -> a).collect(Collectors.toList());
        AtomicInteger sequenceNumber = new AtomicInteger(1);
        return analytics.entrySet().stream().filter(e -> (summaryMap.get(e.getKey()) != null))
                .sorted(Comparator.comparing(value -> value.getValue().intValue(), Comparator.reverseOrder()))
                .limit(4)
                .map(e -> new ReadershipAnalytics(e.getKey(), e.getKey().replace(PDF, ""), e.getValue().intValue(), sequenceNumber.getAndIncrement()))
                .collect(Collectors.toList());
    }

    private static Map<String, ResourceSummary> getResourceSummery() {

        Map<String, ResourceSummary> stringResourceSummaryMap = new HashMap<>();
        stringResourceSummaryMap.put("111.pdf", new ResourceSummary());
        stringResourceSummaryMap.put("112.pdf", new ResourceSummary());
        stringResourceSummaryMap.put("113.pdf", new ResourceSummary());
        stringResourceSummaryMap.put("114.pdf", new ResourceSummary());
        stringResourceSummaryMap.put("115.pdf", new ResourceSummary());
        stringResourceSummaryMap.put("116.pdf", new ResourceSummary());
        stringResourceSummaryMap.put("117.pdf", new ResourceSummary());
        stringResourceSummaryMap.put("118.pdf", new ResourceSummary());
        stringResourceSummaryMap.put("119.pdf", new ResourceSummary());
        stringResourceSummaryMap.put("120.pdf", new ResourceSummary());
        return stringResourceSummaryMap;
    }
}

