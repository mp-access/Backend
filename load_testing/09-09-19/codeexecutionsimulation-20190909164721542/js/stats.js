var stats = {
    type: "GROUP",
name: "Global Information",
path: "",
pathFormatted: "group_missing-name-b06d1",
stats: {
    "name": "Global Information",
    "numberOfRequests": {
        "total": "10973",
        "ok": "10973",
        "ko": "0"
    },
    "minResponseTime": {
        "total": "8",
        "ok": "8",
        "ko": "-"
    },
    "maxResponseTime": {
        "total": "1055",
        "ok": "1055",
        "ko": "-"
    },
    "meanResponseTime": {
        "total": "15",
        "ok": "15",
        "ko": "-"
    },
    "standardDeviation": {
        "total": "27",
        "ok": "27",
        "ko": "-"
    },
    "percentiles1": {
        "total": "11",
        "ok": "11",
        "ko": "-"
    },
    "percentiles2": {
        "total": "12",
        "ok": "12",
        "ko": "-"
    },
    "percentiles3": {
        "total": "20",
        "ok": "20",
        "ko": "-"
    },
    "percentiles4": {
        "total": "145",
        "ok": "145",
        "ko": "-"
    },
    "group1": {
        "name": "t < 800 ms",
        "count": 10972,
        "percentage": 100
    },
    "group2": {
        "name": "800 ms < t < 1200 ms",
        "count": 1,
        "percentage": 0
    },
    "group3": {
        "name": "t > 1200 ms",
        "count": 0,
        "percentage": 0
    },
    "group4": {
        "name": "failed",
        "count": 0,
        "percentage": 0
    },
    "meanNumberOfRequestsPerSecond": {
        "total": "107.578",
        "ok": "107.578",
        "ko": "-"
    }
},
contents: {
"req_post-submission-ff406": {
        type: "REQUEST",
        name: "POST submission",
path: "POST submission",
pathFormatted: "req_post-submission-ff406",
stats: {
    "name": "POST submission",
    "numberOfRequests": {
        "total": "350",
        "ok": "350",
        "ko": "0"
    },
    "minResponseTime": {
        "total": "39",
        "ok": "39",
        "ko": "-"
    },
    "maxResponseTime": {
        "total": "1055",
        "ok": "1055",
        "ko": "-"
    },
    "meanResponseTime": {
        "total": "64",
        "ok": "64",
        "ko": "-"
    },
    "standardDeviation": {
        "total": "65",
        "ok": "65",
        "ko": "-"
    },
    "percentiles1": {
        "total": "46",
        "ok": "46",
        "ko": "-"
    },
    "percentiles2": {
        "total": "52",
        "ok": "52",
        "ko": "-"
    },
    "percentiles3": {
        "total": "158",
        "ok": "158",
        "ko": "-"
    },
    "percentiles4": {
        "total": "257",
        "ok": "257",
        "ko": "-"
    },
    "group1": {
        "name": "t < 800 ms",
        "count": 349,
        "percentage": 100
    },
    "group2": {
        "name": "800 ms < t < 1200 ms",
        "count": 1,
        "percentage": 0
    },
    "group3": {
        "name": "t > 1200 ms",
        "count": 0,
        "percentage": 0
    },
    "group4": {
        "name": "failed",
        "count": 0,
        "percentage": 0
    },
    "meanNumberOfRequestsPerSecond": {
        "total": "3.431",
        "ok": "3.431",
        "ko": "-"
    }
}
    },"req_poll-for-evalua-53bb8": {
        type: "REQUEST",
        name: "Poll for evaluation",
path: "Poll for evaluation",
pathFormatted: "req_poll-for-evalua-53bb8",
stats: {
    "name": "Poll for evaluation",
    "numberOfRequests": {
        "total": "10623",
        "ok": "10623",
        "ko": "0"
    },
    "minResponseTime": {
        "total": "8",
        "ok": "8",
        "ko": "-"
    },
    "maxResponseTime": {
        "total": "505",
        "ok": "505",
        "ko": "-"
    },
    "meanResponseTime": {
        "total": "13",
        "ok": "13",
        "ko": "-"
    },
    "standardDeviation": {
        "total": "23",
        "ok": "23",
        "ko": "-"
    },
    "percentiles1": {
        "total": "11",
        "ok": "11",
        "ko": "-"
    },
    "percentiles2": {
        "total": "12",
        "ok": "12",
        "ko": "-"
    },
    "percentiles3": {
        "total": "16",
        "ok": "16",
        "ko": "-"
    },
    "percentiles4": {
        "total": "29",
        "ok": "29",
        "ko": "-"
    },
    "group1": {
        "name": "t < 800 ms",
        "count": 10623,
        "percentage": 100
    },
    "group2": {
        "name": "800 ms < t < 1200 ms",
        "count": 0,
        "percentage": 0
    },
    "group3": {
        "name": "t > 1200 ms",
        "count": 0,
        "percentage": 0
    },
    "group4": {
        "name": "failed",
        "count": 0,
        "percentage": 0
    },
    "meanNumberOfRequestsPerSecond": {
        "total": "104.147",
        "ok": "104.147",
        "ko": "-"
    }
}
    }
}

}

function fillStats(stat){
    $("#numberOfRequests").append(stat.numberOfRequests.total);
    $("#numberOfRequestsOK").append(stat.numberOfRequests.ok);
    $("#numberOfRequestsKO").append(stat.numberOfRequests.ko);

    $("#minResponseTime").append(stat.minResponseTime.total);
    $("#minResponseTimeOK").append(stat.minResponseTime.ok);
    $("#minResponseTimeKO").append(stat.minResponseTime.ko);

    $("#maxResponseTime").append(stat.maxResponseTime.total);
    $("#maxResponseTimeOK").append(stat.maxResponseTime.ok);
    $("#maxResponseTimeKO").append(stat.maxResponseTime.ko);

    $("#meanResponseTime").append(stat.meanResponseTime.total);
    $("#meanResponseTimeOK").append(stat.meanResponseTime.ok);
    $("#meanResponseTimeKO").append(stat.meanResponseTime.ko);

    $("#standardDeviation").append(stat.standardDeviation.total);
    $("#standardDeviationOK").append(stat.standardDeviation.ok);
    $("#standardDeviationKO").append(stat.standardDeviation.ko);

    $("#percentiles1").append(stat.percentiles1.total);
    $("#percentiles1OK").append(stat.percentiles1.ok);
    $("#percentiles1KO").append(stat.percentiles1.ko);

    $("#percentiles2").append(stat.percentiles2.total);
    $("#percentiles2OK").append(stat.percentiles2.ok);
    $("#percentiles2KO").append(stat.percentiles2.ko);

    $("#percentiles3").append(stat.percentiles3.total);
    $("#percentiles3OK").append(stat.percentiles3.ok);
    $("#percentiles3KO").append(stat.percentiles3.ko);

    $("#percentiles4").append(stat.percentiles4.total);
    $("#percentiles4OK").append(stat.percentiles4.ok);
    $("#percentiles4KO").append(stat.percentiles4.ko);

    $("#meanNumberOfRequestsPerSecond").append(stat.meanNumberOfRequestsPerSecond.total);
    $("#meanNumberOfRequestsPerSecondOK").append(stat.meanNumberOfRequestsPerSecond.ok);
    $("#meanNumberOfRequestsPerSecondKO").append(stat.meanNumberOfRequestsPerSecond.ko);
}
