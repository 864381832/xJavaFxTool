package com.xwintop.xJavaFxTool.services.assistTools;

import com.baidu.aip.speech.AipSpeech;
import com.baidu.aip.speech.TtsResponse;
import com.baidu.aip.util.Util;
import com.xwintop.xJavaFxTool.controller.assistTools.TextToSpeechToolController;
import com.xwintop.xJavaFxTool.utils.ConfigureUtil;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.json.JSONObject;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * @ClassName: TextToSpeechToolService
 * @Description: 语音转换工具
 * @author: xufeng
 * @date: 2018/2/6 15:53
 */

@Getter
@Setter
@Log4j
public class TextToSpeechToolService {
    private TextToSpeechToolController textToSpeechToolController;
    //设置APPID/AK/SK
    public static final String APP_ID = "10690878";
    public static final String API_KEY = "y5VI7DG4YNb1G600X8FpxsN2";
    public static final String SECRET_KEY = "4RoIZEBGePYU4R4capF4OEYZ3HL3nCSU";

    private AudioStream audioStream = null;

    private String mp3Cache = ConfigureUtil.getConfigurePath("output.mp3");

    public void playAction() throws Exception {
        AipSpeech client = new AipSpeech(APP_ID, API_KEY, SECRET_KEY);
        // 设置可选参数
        HashMap<String, Object> options = new HashMap<String, Object>();
        options.put("spd", Integer.toString((int) textToSpeechToolController.getSpdSlider().getValue()));
        options.put("vol", Integer.toString((int) textToSpeechToolController.getVolSlider().getValue()));
        String per = textToSpeechToolController.getPerToggleGroup().getSelectedToggle().getUserData().toString();
        options.put("per", per);
        System.out.println(options.toString());
        // 调用接口
        TtsResponse res = client.synthesis(textToSpeechToolController.getTextTextArea().getText(), "zh", 1, options);
        byte[] data = res.getData();
        JSONObject res1 = res.getResult();
        if (data != null) {
                Util.writeBytesToFileSystem(data, mp3Cache);
//            audioStream = new AudioStream(new ByteArrayInputStream(data));
//            AudioPlayer.player.start(audioStream);//用静态成员player.start播放音乐
            //AudioPlayer.player.stop(as);//关闭音乐播放
            Media media = new Media(new File(mp3Cache).toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
        }
        if (res1 != null) {
            System.out.println(res1.toString(2));
        }           //生成的音频数据
    }

    public void stopPlayAction(){
        if (audioStream != null){
            AudioPlayer.player.stop(audioStream);
        }
    }

    public TextToSpeechToolService(TextToSpeechToolController textToSpeechToolController) {
        this.textToSpeechToolController = textToSpeechToolController;
    }
}