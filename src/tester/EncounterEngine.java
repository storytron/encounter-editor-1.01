package tester;

import java.util.ArrayList;


public class EncounterEngine {
	public static final int Camiggdo = 0;
	public static final int Skordokott = 1;
	public static final int Zubenelgenubi = 2;
	public static final int Koopie = 3;
	public static final int Subotai = 4;
	public static final int cActors = 5;
	public static final int cFactors = 12;
	public static final int cOptions = 5;
	public static final int cReactions = 4;
	public static final int maxEncounters = 500;
	public static final int maxPreDis = 5;	
	//**********************************************************************
	EncounterEngine() {
	}
	//**********************************************************************
	public Encounter getNewEncounter() { return new Encounter(); }
	//**********************************************************************
	
	public class Encounter {
		private String title;
		private String introText;
		private String author;
		private ArrayList<String> prerequisites;
		private ArrayList<String> disqualifiers;
		private Option[] options;
		private boolean[] isAllowedToBeAntagonist;
		private boolean[] isAllowedToBeProtagonist;
		private int firstDay = 1;
		private int lastDay = 20;
	//-----------------------------------------------------------------------
		Encounter() {
			title = "new Encounter";
			introText = "Introductory Text";
			author = "nobody";
			prerequisites = new ArrayList<String>();
			disqualifiers = new ArrayList<String>();
			isAllowedToBeAntagonist = new boolean[cActors];
			isAllowedToBeProtagonist = new boolean[cActors];
			options = new Option[cOptions];
			for (int i=0; (i<cOptions); ++i) {
				options[i] = new Option();
			}
			
			for (int i=0; (i<cActors); ++i) {
				isAllowedToBeAntagonist[i] = true; 
				isAllowedToBeProtagonist[i] = true; 
			}
		}		
	//-----------------------------------------------------------------------
	public Option getNewOption() { return new Option(); }
	//-----------------------------------------------------------------------
	public String getTitle() { return title; }
	//-----------------------------------------------------------------------
	public void setTitle(String newTitle) { title = newTitle; }
	//-----------------------------------------------------------------------
	public String getIntroText() { return introText; }
	//-----------------------------------------------------------------------
	public void setIntroText(String newText) { introText = newText; }
	//-----------------------------------------------------------------------
	public String getAuthor() { return author; }
	//-----------------------------------------------------------------------
	public void setAuthor(String newAuthor) { author = newAuthor; }
	//-----------------------------------------------------------------------
	public ArrayList<String> getPrerequisites() { return prerequisites; }
	//-----------------------------------------------------------------------
	public void setPrerequisites(ArrayList<String> newPrerequisites) { prerequisites = newPrerequisites; }
	//-----------------------------------------------------------------------
	public ArrayList<String> getDisqualifiers() { return disqualifiers; }
	//-----------------------------------------------------------------------
	public void setDisqualifiers(ArrayList<String> newDisqualifiers) { disqualifiers = newDisqualifiers; }
	//-----------------------------------------------------------------------
	public Option getOption(int index) { return options[index]; }
	//-----------------------------------------------------------------------
	public void setOption(Option newOption, int index) { options[index] = newOption; }
	//-----------------------------------------------------------------------
	public boolean getIsAllowedToBeAntagonist(int index) { return isAllowedToBeAntagonist[index]; }
	//-----------------------------------------------------------------------
	public void setIsAllowedToBeAntagonist(boolean newValue, int index) { isAllowedToBeAntagonist[index] = newValue; }
	//-----------------------------------------------------------------------
	public boolean getIsAllowedToBeProtagonist(int index) { return isAllowedToBeProtagonist[index]; }
	//-----------------------------------------------------------------------
	public void setIsAllowedToBeProtagonist(boolean newValue, int index) { isAllowedToBeProtagonist[index] = newValue; }
	//-----------------------------------------------------------------------
	public int getFirstDay() { return firstDay; }
	//-----------------------------------------------------------------------
	public void setFirstDay(int newValue) { firstDay = newValue; }
	//-----------------------------------------------------------------------
	public int getLastDay() { return lastDay; }
	//-----------------------------------------------------------------------
	public void setLastDay(int newValue) { lastDay = newValue; }
	//-----------------------------------------------------------------------
	//-----------------------------------------------------------------------
	//-----------------------------------------------------------------------
	//-----------------------------------------------------------------------
	}
	//**********************************************************************
	public class Option {
		private String text;
//			private String trait1PBad_Good, trait2PBad_Good;
//			private String trait1PFalse_Honest, trait2PFalse_Honest;
//			private String trait1PTimid_Dominant, trait2PTimid_Dominant;
		private float deltaPBad_Good, deltaPFalse_Honest, deltaPTimid_Dominant;
		private Reaction[] reactions;
	//-----------------------------------------------------------------------
		Option() {
			text = "Unused option";
			deltaPBad_Good = 0.0f;
			deltaPFalse_Honest = 0.0f;
			deltaPTimid_Dominant = 0.0f;
//				trait1PBad_Good = "Bad_Good";
//				trait2PBad_Good = "Bad_Good";
//				trait1PFalse_Honest = "Bad_Good";
//				trait2PFalse_Honest = "Bad_Good";
//				trait1PTimid_Dominant = "Bad_Good";
//				trait2PTimid_Dominant = "Bad_Good";
			reactions = new Reaction[cReactions];
			for (int i=0; (i<cReactions); ++i) {
				reactions[i] = new Reaction();
			}
		}
	//-----------------------------------------------------------------------
		public Reaction getNewReaction() { return new Reaction(); }
	//-----------------------------------------------------------------------
		public String getText() { return text; }
	//-----------------------------------------------------------------------
		public void setText(String newText) { text = newText; }
	//-----------------------------------------------------------------------
		public Reaction getReaction(int index) { return reactions[index]; }
	//-----------------------------------------------------------------------
		public void setReaction (int index, Reaction newReaction) { reactions[index] = newReaction; }
	//-----------------------------------------------------------------------
		public float getDeltaPBad_Good() { return deltaPBad_Good; }
	//-----------------------------------------------------------------------
		public void setDeltaPBad_Good(float newValue) { deltaPBad_Good = newValue; }
	//-----------------------------------------------------------------------
		public float getDeltaPFalse_Honest() { return deltaPFalse_Honest; }
	//-----------------------------------------------------------------------
		public void setDeltaPFalse_Honest(float newValue) { deltaPFalse_Honest = newValue; }
	//-----------------------------------------------------------------------
		public float getDeltaPTimid_Dominant() { return deltaPTimid_Dominant; }
	//-----------------------------------------------------------------------
		public void setDeltaPTimid_Dominant(float newValue) { deltaPTimid_Dominant = newValue; }
	//-----------------------------------------------------------------------
//			public String getTrait1PBad_Good() { return trait1PBad_Good; }
	//-----------------------------------------------------------------------
//			public void setTrait1PBad_Good(String newValue) { trait1PBad_Good = newValue; }
	//-----------------------------------------------------------------------
//			public String getTrait2PBad_Good() { return trait2PBad_Good; }
	//-----------------------------------------------------------------------
//			public void setTrait2PBad_Good(String newValue) { trait2PBad_Good = newValue; }
	//-----------------------------------------------------------------------
//			public String getTrait1PFalse_Honest() { return trait1PFalse_Honest; }
	//-----------------------------------------------------------------------
//			public void setTrait1PFalse_Honest(String newValue) { trait1PFalse_Honest = newValue; }
	//-----------------------------------------------------------------------
//			public String getTrait2PFalse_Honest() { return trait2PFalse_Honest; }
	//-----------------------------------------------------------------------
//			public void setTrait2PFalse_Honest(String newValue) { trait2PFalse_Honest = newValue; }
		//-----------------------------------------------------------------------
//			public String getTrait1PTimid_Dominant() { return trait1PTimid_Dominant; }
	//-----------------------------------------------------------------------
//			public void setTrait1PTimid_Dominant(String newValue) { trait1PTimid_Dominant = newValue; }
	//-----------------------------------------------------------------------
//			public String getTrait2PTimid_Dominant() { return trait2PTimid_Dominant; }
	//-----------------------------------------------------------------------
//			public void setTrait2PTimid_Dominant(String newValue) { trait2PTimid_Dominant = newValue; }
	//-----------------------------------------------------------------------
	//-----------------------------------------------------------------------
	//-----------------------------------------------------------------------
	//-----------------------------------------------------------------------
	}
	//-----------------------------------------------------------------------
//**********************************************************************
	public class Reaction {
		private String firstTrait;
		private String secondTrait;
		private float bias;
		private String text;
	//-----------------------------------------------------------------------
		Reaction() {
			firstTrait = "Bad_Good";
			secondTrait = "False_Honest";
			bias = 0.0f;
			text = "unused Reaction";
		}
	//-----------------------------------------------------------------------
			public String getFirstTrait() { return firstTrait; }
	//-----------------------------------------------------------------------
			public void setFirstTrait(String newValue) { firstTrait = newValue; }
	//-----------------------------------------------------------------------
			public String getSecondTrait() { return secondTrait; }
	//-----------------------------------------------------------------------
			public void setSecondTrait(String newValue) { secondTrait = newValue; }
	//-----------------------------------------------------------------------
			public float getBias() { return bias; }
	//-----------------------------------------------------------------------
			public void setBias(float newValue) { bias = newValue; }
	//-----------------------------------------------------------------------
			public String getText() { return text; }
	//-----------------------------------------------------------------------
			public void setText(String newValue) { text = newValue; }
	//-----------------------------------------------------------------------
	//-----------------------------------------------------------------------
	//-----------------------------------------------------------------------
	//-----------------------------------------------------------------------
	}
//**********************************************************************
	public float blend(float x1, float x2, float z) {
		float bWeightingFactor = z;
		if (bWeightingFactor <= -1.00f) {
			bWeightingFactor = -1.00f;
		}
		if (bWeightingFactor >= 1.00f) {
			bWeightingFactor = 1.00f;
		}
		// this is a conversion from BNumber to UNumber
		float uWeightingFactor = (1.0f+bWeightingFactor)/2.0f;
		
		return(x2*uWeightingFactor + x1*(1.0f-uWeightingFactor));
	}

}
