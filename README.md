# IoT_Network

### 프로젝트 설명📝
* API를 활용한 실시간 날씨와 기타 기상 정보를 알 수 있는 기상 정보 시스템 웹 서비스

### 프로젝트 사용 언어💻
* 백엔드: JAVA, Node.Js, Mongo DB
* 프론트엔드:CSS, HTML
* 통신방식: MQTT 방식

### MQTT 서버 통신 구현 코드📲

```JAVA
public void connectBroker() {//(브로커 연결)
    String broker = "tcp://127.0.0.1:1883"; // <-- 서버 주소
    String clientId = "practice";
    ...
    sampleClient = new MqttClient(broker, clientId, persistence);
    sampleClient.connect(connOpts); // <-- 서버 연결
    sampleClient.setCallback(this); // <-- 콜백 설정 (메시지 도착 시 실행될 메서드)
}
```
```
public void publish_data(String topic_input, String data) { //메시지 발행(Publish)
    ...
    sampleClient.publish(topic, data.getBytes(), qos, false); // <-- 서버로 데이터 전송
}
```
```
sampleClient.subscribe("led"); // <-- "led" 토픽을 구독하고 메시지를 기다림 //메시지 구독(Subscribe)
```
