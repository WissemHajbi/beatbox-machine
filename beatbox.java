package BeatBoxMainPackage;
import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Main {
    String[] instrumentsName={"Bass Drum","Closed Hi-Hat","Open Hi-hat","Acoustic Snare",
            "Crash Symbol","Hand Clap","High Tom","Hi Bongo","Maracas","Whistle","Low Conga","Cowbell"
            ,"Vibrates Lap","Low-mid Tom","High Agog","Open Hi Conga"};
    int[] instruments={36,42,46,33,49,39,50,60,70,72,64,56,58,47,67,63};
    JPanel mainPanel;
    ArrayList<JCheckBox> checkBoxList;
    Sequencer sequencer;
    Sequence sequence;
    Track track;
    JFrame frame;

    public void buildGui(){
        frame=new JFrame("BeatBox Machine 1.0 BETA");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout=new BorderLayout();
        JPanel background=new JPanel(layout);
        background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        checkBoxList=new ArrayList<>();
        GridLayout grid=new GridLayout(16,16);
        grid.setVgap(10);
        grid.setHgap(10);
        JPanel buttonPanel=new JPanel(grid);
        JButton Start=new JButton("Start");
        Start.addActionListener(new MyStartListener());
        buttonPanel.add(Start);
        JButton Stop=new JButton("Stop");
        Stop.addActionListener(new MyStopListener());
        buttonPanel.add(Stop);
        JButton TempoUp=new JButton("Tempo UP");
        TempoUp.addActionListener(new MyTempoUpListener());
        buttonPanel.add(TempoUp);
        JButton TempoDown=new JButton("Tempo DOWN");
        TempoDown.addActionListener(new MyTempoDownListener());
        buttonPanel.add(TempoDown);
        JButton clear=new JButton("Clear");
        clear.addActionListener(new MyClearListener());
        buttonPanel.add(clear);
        JButton random=new JButton("Random");
        random.addActionListener(new MyRandomListener());
        buttonPanel.add(random);

        JPanel namesPanel =new JPanel(grid);
        for(int i=0;i<16;i++){
            JLabel label=new JLabel(instrumentsName[i]);
            namesPanel.add(label);
        }

        background.add(BorderLayout.EAST,buttonPanel);
        background.add(BorderLayout.WEST,namesPanel);

        frame.add(background);


        mainPanel =new JPanel(grid);
 //       GradientPaint paint=new GradientPaint(10,10,Color.black,50,50,Color.gray);
//        mainPanel.setColor(paint);
        background.add(BorderLayout.CENTER,mainPanel);

        for(int i=0;i<256;i++){
            JCheckBox check=new JCheckBox();
            check.setSelected(false);
            checkBoxList.add(check);
            mainPanel.add(check);
        }

        setUpMidi();

        frame.setBounds(100,100,300,300);
        frame.pack();
        frame.setVisible(true);

    }
//TODO setupgui bch nabda
    public void setUpMidi(){
        try{
            sequencer=MidiSystem.getSequencer();
            sequencer.open();
            sequence=new Sequence(Sequence.PPQ,4);
            track=sequence.createTrack();
            sequencer.setTempoInBPM(120);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void buildTrackAndPlay(){
        int[] trackList;
        sequence.deleteTrack(track);
        track=sequence.createTrack();
        for(int i=0;i<16;i++){
            trackList=new int[16];
            int key=instruments[i];
            for(int j=0;j<16;j++){
                JCheckBox jc=checkBoxList.get(j+16*i);
                if(jc.isSelected()){
                    trackList[j]=key;
                }else{
                    trackList[j]=0;
                }
            }
            MakeTracks(trackList);
            track.add(MakeEvent(176,1,127,0,16));
        }
            track.add(MakeEvent(192,9,1,0,15));
        try{
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
            sequencer.setTempoInBPM(120);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
//        public class mainPanel extends JPanel{
//        protected void paintComponent(Graphics g){
//            GradientPaint paint=new GradientPaint(10,10,Color.black,50,50,Color.gray);
//            mainPanel.setColor(paint);
//        }
//    }

    public class MyRandomListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            for(int i=0;i<256;i++){
                checkBoxList.get(i).setSelected(false);
            }
            int rand=(int)(Math.random()*20);
            for(int i=0;i<rand;i++){
                    rand=(int)(Math.random()*256);
                    checkBoxList.get(rand).setSelected(true);
            }
            
        }
    }
    public class MyStartListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            buildTrackAndPlay();
        }
    }
    public class MyStopListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            sequencer.stop();
        }
    }
    public class MyTempoUpListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            float tempo=sequencer.getTempoFactor();
            sequencer.setTempoFactor((float)(tempo * 1.03));
            }
    }
    public class MyTempoDownListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            float tempo = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempo * 0.79));
        }
    }
    public class MyClearListener implements ActionListener {
        public void actionPerformed(ActionEvent e){
            for(int i=0;i<256;i++){
                        checkBoxList.get(i).setSelected(false);
            }
            sequencer.stop();
        }
    }

    public void MakeTracks(int[] list){
        for(int i=0;i<16;i++){
            int key=list[i];
            if(key!=0){
                track.add(MakeEvent(144,9,key,100,i));
                track.add(MakeEvent(128,9,key,100,i+1));
            }
        }
    }

    public MidiEvent MakeEvent(int comd, int chan, int one, int two, int tick){
        MidiEvent event=null;
        try{
            ShortMessage a=new ShortMessage();
            a.setMessage(comd,chan,one,two);
            event=new MidiEvent(a,tick);
        }catch(Exception ignored){ }
        return event;
    }

}