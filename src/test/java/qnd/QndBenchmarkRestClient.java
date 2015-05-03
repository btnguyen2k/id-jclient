package qnd;

import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.transport.TTransportException;

import test.utils.Benchmark;
import test.utils.BenchmarkResult;
import test.utils.Operation;

import com.github.btnguyen2k.id.jclient.IIdClient;
import com.github.btnguyen2k.id.jclient.impl.RestIdClientFactory;

public class QndBenchmarkRestClient extends BaseQndThriftClient {

    private static void runTest(final IIdClient client, final int numRuns, final int numThreads,
            final int numNamespaces) throws TTransportException {
        BenchmarkResult result = new Benchmark(new Operation() {
            @Override
            public void run(int runId) {
                client.nextId("default");
            }
        }, numRuns, numThreads).run();
        System.out.println(result.summarize());
    }

    /**
     * @param args
     * @throws TTransportException
     */
    public static void main(String[] args) throws TTransportException {
        String idServerUrl = System.getProperty("idServerUrl");
        if (StringUtils.isBlank(idServerUrl)) {
            idServerUrl = "http://localhost:9000";
        }

        int numRuns, numThreads, numNamespaces;

        try {
            numRuns = Integer.parseInt(System.getProperty("numRuns"));
        } catch (Exception e) {
            numRuns = 100000;
        }
        try {
            numThreads = Integer.parseInt(System.getProperty("numThreads"));
        } catch (Exception e) {
            numThreads = 4;
        }
        try {
            numNamespaces = Integer.parseInt(System.getProperty("numNamespaces"));
        } catch (Exception e) {
            numNamespaces = 4;
        }

        for (int i = 0; i < 10; i++) {
            runTest(RestIdClientFactory.newIdClient(idServerUrl), numRuns, numThreads,
                    numNamespaces);
        }

        RestIdClientFactory.cleanup();
    }
}
