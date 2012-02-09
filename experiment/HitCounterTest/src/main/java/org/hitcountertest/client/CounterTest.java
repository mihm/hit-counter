package org.hitcountertest.client;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CounterTest {
    private static final Logger LOGGER = Logger
            .getLogger(CounterTest.class);

    private static int errorsCount;

    public static void main(String[] args) throws
            SAXException, InterruptedException, MalformedURLException {
        //100.000
        int totalCount = 100000;
        run(1, totalCount);
        run(5, totalCount / 5);
        run(10, totalCount / 10);
        run(15, totalCount / 15);
        run(30, totalCount / 30);
        run(50, totalCount / 50);
        run(80, totalCount / 80);
        run(100, totalCount / 100);
        run(120, totalCount / 120);
        run(140, totalCount / 140);
        run(180, totalCount / 180);
        run(200, totalCount / 200);
        run(250, totalCount / 250);
        run(300, totalCount / 300);
        run(350, totalCount / 350);
        run(400, totalCount / 400);
        run(500, totalCount / 500);
        run(700, totalCount / 700);
        run(1000, totalCount / 1000);

    }

    public static void run(int threadsCount, final int iterations) throws InterruptedException, MalformedURLException {
//        final WebConversation conversation = new WebConversation();
//        final WebRequest request = new PostMethodWebRequest(
//                "http://192.168.1.243:8080/Counter_war/CounterServlet");

        final URL url = new URL("http://192.168.1.243:8080/Counter_war/CounterServlet");

        Thread[] threads = new Thread[threadsCount];

        errorsCount = 0;

        final List<Long> mls = Collections
                .synchronizedList(new ArrayList<Long>(threadsCount));

        for (int i = 0; i < threadsCount; i++) {
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    long start = System.currentTimeMillis();

                    for (int i = 0; i < iterations; i++) {
                        try {
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("POST");
//                            connection.setDoOutput(true);

                            connection.connect();
                            InputStream inputStream = connection.getInputStream();
//                            while (inputStream.read() > 0) {
//                            }

                            connection.disconnect();

                        } catch (Throwable e) {
//                            LOGGER.error(e.getMessage(), e);
                            errorsCount++;
                        }

                    }

                    long end = System.currentTimeMillis();
                    mls.add(end - start);

                }
            });
            thread.start();

            threads[i] = thread;
        }

        for (int i = 0; i < threadsCount; i++) {
            threads[i].join();
        }

        double sum = 0;
        for (Long ml : mls) {
            sum += ml;
        }

        LOGGER.info("------------------------------- ");
        LOGGER.info("Threads count: " + threadsCount);
        LOGGER.info("iterations/thread: " + iterations);
        LOGGER.info("Total time(mls): " + sum);
        LOGGER.info("Mls/thread : " + sum / threadsCount);
        LOGGER.info("Mls/request : " + sum / threadsCount / iterations);
        LOGGER.info("Errors count : " + errorsCount);
        LOGGER.info("------------------------------- ");

        for (int i = 0; i < threadsCount; i++) {
            threads[i].interrupt();
        }
        System.gc();
    }
}