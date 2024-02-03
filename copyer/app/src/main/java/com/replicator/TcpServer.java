package com.replicator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TcpServer extends AsyncTask<Void, String, Void> {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private BufferedReader inputReader;
    public static ArrayDeque<String> queue = new ArrayDeque<>();
    private OkHttpClient client=new OkHttpClient();
    public void startServer1(){
         sendGetRequest("http://192.168.140.10:33333");
//        Log.d("sdfsd====",result);
//        System.out.println("==========="+result);
//        handleMessage(result);
    }
    private void sendGetRequest(String url) {
        Request request = new Request.Builder()
                .url(url)

                .build();

        System.out.println("======="+request);
//        client = new OkHttpClient.Builder()
//                .sslSocketFactory(sslSocketFactory, trustManager)
//                .hostnameVerifier((hostname, session) -> true)
//                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    // 在这里处理响应数据
                    handleMessage(responseData);
                    Log.d("=======Response", responseData);
                } else {
                    // 处理请求失败的情况
                    Log.d("Response", "Request failed");
                }
            }
        });
    }
    public void startServer() {
        try {
            // 创建 ServerSocket 对象，并绑定端口号
            serverSocket = new ServerSocket(33281);

            // 等待客户端连接
            clientSocket = serverSocket.accept();

            // 获取输入流，用于接收客户端发送的消息
            inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // 循环接收消息
            String message;
            while ((message = inputReader.readLine()) != null) {
                // 处理接收到的消息
                handleMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (inputReader != null) {
                    inputReader.close();
                }
                if (clientSocket != null) {
                    clientSocket.close();
                }
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public  void copyToClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", text);
        clipboard.setPrimaryClip(clip);
    }
    private void handleMessage(String message) {
        // 在这里处理接收到的消息
        // 可以将消息显示在界面上或者进行其他操作
        enqueue(message);
        copyToClipboard(MainActivity.mainActivity,dequeue());
    }

    public void stopServer() {
        try {
            // 关闭资源
            if (inputReader != null) {
                inputReader.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        while(true){
            try {
                Thread.sleep(500);
                startServer1();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    public void enqueue(String item) {
        queue.offer(item);  // 将元素添加到队列末尾
    }

    public String dequeue() {
        return queue.poll();  // 获取并移除队列头部的元素
    }
}
