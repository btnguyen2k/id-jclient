//package qnd;
//
//import org.apache.commons.lang3.StringUtils;
//import org.apache.thrift.transport.TTransportException;
//
//import test.utils.Benchmark;
//import test.utils.BenchmarkResult;
//import test.utils.Operation;
//
//import com.github.btnguyen2k.idserver.thrift.TIdService;
//import com.github.ddth.id.jclient.thrift.ThriftClientFactory;
//import com.github.ddth.thriftpool.ThriftClientPool;
//
//public class QndBenchmarkThriftClientPing extends BaseQndThriftClient {
//
//    private static void runTest(
//            final ThriftClientPool<TIdService.Client, TIdService.Iface> clientPool,
//            final int numRuns, final int numThreads, final int numNamespaces)
//            throws TTransportException {
//        BenchmarkResult result = new Benchmark(new Operation() {
//            @Override
//            public void run(int runId) {
//                try {
//                    TIdService.Iface client = clientPool.borrowObject();
//                    try {
//                        client.ping();
//                    } finally {
//                        clientPool.returnObject(client);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }, numRuns, numThreads).run();
//        System.out.println(result.summarize());
//    }
//
//    /**
//     * @param args
//     * @throws TTransportException
//     */
//    public static void main(String[] args) throws TTransportException {
//        String thriftHost = System.getProperty("thriftHost");
//        if (StringUtils.isBlank(thriftHost)) {
//            thriftHost = "localhost";
//        }
//        int thriftPort;
//        try {
//            thriftPort = Integer.parseInt(System.getProperty("thriftPort"));
//        } catch (Exception e) {
//            thriftPort = 9090;
//        }
//        final ThriftClientPool<TIdService.Client, TIdService.Iface> clientPool = ThriftClientFactory
//                .newDefaultClientPool(thriftHost, thriftPort);
//
//        int numRuns, numThreads, numNamespaces;
//
//        try {
//            numRuns = Integer.parseInt(System.getProperty("numRuns"));
//        } catch (Exception e) {
//            numRuns = 100000;
//        }
//        try {
//            numThreads = Integer.parseInt(System.getProperty("numThreads"));
//        } catch (Exception e) {
//            numThreads = 4;
//        }
//        try {
//            numNamespaces = Integer.parseInt(System.getProperty("numNamespaces"));
//        } catch (Exception e) {
//            numNamespaces = 4;
//        }
//
//        for (int i = 0; i < 10; i++) {
//            runTest(clientPool, numRuns, numThreads, numNamespaces);
//        }
//
//        clientPool.destroy();
//    }
//}
