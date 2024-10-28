package Test;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MqttPublisher_API implements MqttCallback {
    static MqttClient sampleClient;

    public static void main(String[] args) {
        MqttPublisher_API obj = new MqttPublisher_API();
        obj.run();
    }

    public void run() {
        connectBroker();
        try {
            sampleClient.subscribe("led");
        } catch (MqttException e1) {
            e1.printStackTrace();
        }
        while (true) {
            try {
                String[] weather_data = get_weather_data();
                publish_data("temp", "{\"temp\": " + weather_data[0] + "}");
                publish_data("wsd", "{\"wsd\": " + weather_data[1] + "}");
                publish_data("rainf", "{\"rainf\": " + weather_data[2] + "}");
                publish_data("pre", "{\"pre\": " + weather_data[3] + "}");
                publish_data("humi", "{\"humi\": " + weather_data[4] + "}");
                publish_data("baseDate", "{\"baseDate\": " + weather_data[5] + "}");
                Thread.sleep(5000);
            } catch (Exception e) {
                try {
                    sampleClient.disconnect();
                } catch (MqttException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
                System.out.println("Disconnected");
                System.exit(0);
            }
        }
    }

    public void connectBroker() {
        String broker = "tcp://127.0.0.1:1883";
        String clientId = "practice";
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: " + broker);
            sampleClient.connect(connOpts);
            sampleClient.setCallback(this);
            System.out.println("Connected");
        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }

    public void publish_data(String topic_input, String data) {
        String topic = topic_input;
        int qos = 0;
        try {
            System.out.println("Publishing message: " + data);
            sampleClient.publish(topic, data.getBytes(), qos, false);
            System.out.println("Message published");
        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }

    public String[] get_weather_data() {
        Date current = new Date(System.currentTimeMillis());
        SimpleDateFormat d_format = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = d_format.format(current).substring(0, 8);
        String time = d_format.format(current).substring(8, 10);

        String base_time;
        // 현재 시간을 기준으로 1시간 전의 데이터를 가져옵니다.
        int hour = Integer.parseInt(time);
        if (hour % 3 == 0) {
            base_time = String.format("%02d00", hour);
        } else {
            base_time = String.format("%02d00", (hour / 3) * 3);
        }

        String url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst"
                + "?serviceKey=IHEe1%2F6Qm22JcYfAVyemRyPpwv2Gk82crDMFzOEVAiee%2BoDWcEW2HGbSDrmqdM0mIjKnmwkV6b3EpVc0tVyM7g%3D%3D"
                + "&pageNo=1&numOfRows=1000"
                + "&dataType=XML"
                + "&base_date=" + date
                + "&base_time=" + base_time
                + "&nx=55"
                + "&ny=127";

        String temp = "";
        String wsd = "";
        String rainf = "";
        String pre = "";
        String humi = "";
        String baseDate = date;
        Document doc = null;

        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (doc != null) {
            Elements elements = doc.select("item");
            for (Element e : elements) {
                String category = e.select("category").text();
                String obsrValue = e.select("obsrValue").text();
                System.out.println("Category: " + category + ", Value: " + obsrValue); // 디버깅을 위한 출력

                if (category.equals("T1H")) {
                    temp = obsrValue;
                }
                if (category.equals("WSD")) {
                    wsd = obsrValue;
                }
                if (category.equals("RN1")) {
                    rainf = obsrValue;
                }
                if (category.equals("PTY")) {
                    pre = obsrValue;
                }
                if (category.equals("REH")) {
                    humi = obsrValue;
                }
            }
        }

        String[] out = {temp, wsd, rainf, pre, humi, baseDate};
        return out;
    }

    @Override
    public void connectionLost(Throwable arg0) {
        System.out.println("Connection lost");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken arg0) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage msg) throws Exception {
        if (topic.equals("led")) {
            System.out.println("--------------------Actuator Function--------------------");
            System.out.println("LED Display changed");
            System.out.println("LED: " + msg.toString());
            System.out.println("---------------------------------------------------------");
        }
    }
}
