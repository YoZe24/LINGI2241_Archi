package Client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientThread implements Runnable{
    private final String id;
    private final int nb;
    private AtomicInteger CPT_SEND;
    private AtomicInteger CPT_RECEIVE;

    long time[];
    public ClientThread(String id, int nb) {
        this.id = id;
        this.nb = nb;
        this.CPT_SEND = new AtomicInteger(0);
        this.CPT_RECEIVE = new AtomicInteger(0);
    }

    @Override
    public void run() {
        time = new long[ClientLaunch.NB_REQUEST + 1];
        Thread[] threads = new Thread[nb];
        for (int i = 0; i < nb; i++) {
            waitingTime();
            threads[i] = createRequest();
            threads[i].start();
        }

        for (int i = 0; i < nb; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    public Thread createRequest(){
        return new Thread(() -> {
            try (Socket socket = new Socket(ClientLaunch.host, ClientLaunch.port);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            )
            {
                String fromServer;
                String fromUser;

                // Client request (On prend une requête au hasard)
                int random = (int) (Math.random() * ClientLaunch.requestsDB.size());
                fromUser = ClientLaunch.requestsDB.get(random);

                int cptSend = CPT_SEND.get();
//                time[cptSend] = System.currentTimeMillis();
                long start = System.currentTimeMillis();
                out.println(this.id);
                out.println(fromUser);
                System.out.println("Client " + id + " : send the "+cptSend + "th request");
                CPT_SEND.getAndIncrement();
                // Server answer
                while ((fromServer = in.readLine()) != null) {
                    //System.out.println(fromServer);
                    if (fromServer.equals("")) {
                        time[cptSend] = System.currentTimeMillis() - start;
                        ClientLaunch.times.add(System.currentTimeMillis()-start);
                        System.out.println("-> : Client #" + id + " waited : " + time[cptSend] + "ms, "+cptSend+"th request");
                        CPT_RECEIVE.getAndIncrement();
                        break;
                    }
                }
            } catch (UnknownHostException e) {
                System.err.println("Don't know about host " + ClientLaunch.host);
                //System.exit(1);
            } catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection to " + ClientLaunch.host);
            }

            if(CPT_RECEIVE.get() == nb && CPT_SEND.get() == nb) {
                ClientLaunch.CPT.addAndGet(CPT_RECEIVE.get()) ;
                System.out.println("#Client sending " + id + " finished, nb request sended " + ClientLaunch.CPT.get() + " -------------------------------------------");
                ClientLaunch.avg += Arrays.stream(time).average().getAsDouble();
            }
        });

    }

    public void waitingTime(){
        double sleep = (ClientLaunch.exponential(ClientLaunch.LAMBDA));
        try {
            Thread.sleep((long) sleep);
            System.out.println(id + " : " +sleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
