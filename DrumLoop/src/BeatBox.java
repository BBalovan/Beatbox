import java.awt.*;
import javax.swing.*;
import javax.sound.midi.*;

import java.util.*;
import java.awt.event.*;
import java.io.IOException;

public class BeatBox {

	JPanel mainpanel;
	ArrayList<JCheckBox> checkBoxList; // Jelölőnégyzetek egy tömblistában
	Sequencer sequencer;
	Sequence sequence;
	Track track;
	JFrame theFrame;

	String[] instrumentNames = { "Lábdob", "Closed lábcin", "Nyitott lábcin",
			"Pergő", "Crash", "Taps", "Kis tam", "Bongo", "Maracas", "Fütty",
			"Konga", "Kolomp", "Slap", "Középmély tam", "Agogo", "Magas konga" };
	int[] instruments = { 35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58,
			47, 67, 63 };

	public static void main(String[] args) {
		new BeatBox().buildGUI();

	}

	public void buildGUI() {
		theFrame = new JFrame("Dobgép");
		theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		BorderLayout layout = new BorderLayout();
		JPanel background = new JPanel(layout);
		background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		checkBoxList = new ArrayList<JCheckBox>();
		Box buttonBox = new Box(BoxLayout.Y_AXIS);

		JButton start = new JButton("Start");
		start.addActionListener(new MyStartListener());
		buttonBox.add(start);

		JButton stop = new JButton("Stop");
		stop.addActionListener(new MyStopListener());
		buttonBox.add(stop);

		JButton upTempo = new JButton("Tempo Up");
		stop.addActionListener(new MyUpTempoListener());
		buttonBox.add(upTempo);

		JButton downTempo = new JButton("Tempo Down");
		stop.addActionListener(new MyDownTempoListener());
		buttonBox.add(downTempo);

		Box nameBox = new Box(BoxLayout.Y_AXIS);
		for (int i = 0; i < 16; i++) {
			nameBox.add(new Label(instrumentNames[i]));
		}

		background.add(BorderLayout.EAST, buttonBox);
		background.add(BorderLayout.WEST, nameBox);

		theFrame.getContentPane().add(background);

		GridLayout grid = new GridLayout(16, 16);
		grid.setVgap(1);
		grid.setHgap(2);
		mainpanel = new JPanel(grid);
		background.add(BorderLayout.CENTER, mainpanel);

		for (int i = 0; i < 256; i++) {
			JCheckBox c = new JCheckBox();
			c.setSelected(false);
			checkBoxList.add(c);
			mainpanel.add(c);
		}
		//Hjkalasd

		setUpMidi();

		theFrame.setBounds(50, 50, 300, 300);
		theFrame.pack();
		theFrame.setVisible(true);
	}

	public void setUpMidi() {
		try {
			sequencer = MidiSystem.getSequencer();
			sequencer.open();
			sequence = new Sequence(Sequence.PPQ, 4);
			track = sequence.createTrack();
			sequencer.setTempoInBPM(80);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void buildTrackAndStart() {
		int[] tracklist = null;

		sequence.deleteTrack(track);
		track = sequence.createTrack();

		for (int i = 0; i < 16; i++) {
			tracklist = new int[16];
			int key = instruments[i];

			for (int j = 0; j < 16; j++) {
				JCheckBox jc = (JCheckBox) checkBoxList.get(j + (16 * i));
				if (jc.isSelected()) {
					tracklist[j] = key;
				} else {
					tracklist[j] = 0;
				}
			}
			makeTracks(tracklist);
			track.add(makeEvent(176, 1, 127, 0, 16));
		}

		track.add(makeEvent(192, 9, 1, 0, 15));
		try {
			sequencer.setSequence(sequence);
			sequencer.setLoopCount(sequencer.LOOP_CONTINUOUSLY);
			sequencer.setTempoInBPM(20);
			sequencer.start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class MyStartListener implements ActionListener {
		public void actionPerformed(ActionEvent a) {
			buildTrackAndStart();
		}
	}

	public class MyStopListener implements ActionListener {
		public void actionPerformed(ActionEvent a) {
			sequencer.stop();
		}
	}

	public class MyUpTempoListener implements ActionListener {
		public void actionPerformed(ActionEvent a) {
			float tempoFactor = sequencer.getTempoFactor();
			sequencer.setTempoFactor((float) (tempoFactor * 1.03));
		}
	}

	public class MyDownTempoListener implements ActionListener {
		public void actionPerformed(ActionEvent a) {
			float tempoFactor = sequencer.getTempoFactor();
			sequencer.setTempoFactor((float) (tempoFactor * 0.97));
		}
	}
	
	
	public void makeTracks(int[] list) {
		for (int i = 0; i < 16; i++) {
			int key  = list[i];
			
			if (key != 0) {
				track.add(makeEvent(144,9,key,100, i));
				track.add(makeEvent(128,9,key,100,i+1));
				
			}
		}
	}
	
	public MidiEvent makeEvent(int comd, int chan, int one, int two, int thick) {
		MidiEvent event = null;
		try {
			ShortMessage a = new ShortMessage();
			a.setMessage(comd, chan, one, two);
			event = new MidiEvent(a, thick);
		} catch (Exception e) {e.printStackTrace(); }
		return event;
	}
}
