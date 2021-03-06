package se.lth.cs.srl.corpus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import se.lth.cs.srl.Learn;

public class Word implements Serializable{
	private static final long serialVersionUID = 12;
		
	public static enum WordData { Form, Lemma, POS, Deprel, Pred };
	
	final Sentence mySentence;
	String[] args;
	
	String Form;
	String Lemma;
	String POS;
	String Deprel;
	String Feats;
	
	int headID;
	Word head;
	
	Set<Word> children;
	boolean isBOS;
	
	Word potentialArgument; //This is basically an attribute of Predicate rather than Word,
							//but this way things work smoother with the features.
	
	final int idx;
	
	//BOS constructor.
	public Word(Sentence s) {
		idx=0;
		isBOS=true;
		children=new HashSet<Word>();
		this.Form="<ROOT-FORM>";
		this.Lemma="<ROOT-LEMMA>";
		this.POS="<ROOT-POS>";
		this.Feats="<ROOT-FEATS>";
		this.Deprel="<ROOT-DEPREL>";
		this.headID=-1;
		
		this.Form="";
		this.Lemma="";
		this.POS="";
		this.Feats="";
		this.Deprel="";
		this.mySentence=s;
	}
	
	public Word(String form,String lemma,String POS,String feats,Sentence mySentence,int idx){
		this.idx=idx;
		this.Form=form;
		this.Lemma=lemma==null?"_":lemma;
		this.POS=POS==null?"_":POS;
		this.Feats=feats==null?"_":feats;
		this.mySentence=mySentence;
		//children=new HashSet<Word>();
		if(Learn.learnOptions!=null && Learn.learnOptions.deterministicPipeline){
			children=new TreeSet<Word>(mySentence.wordComparator);
		} else {
			children=new HashSet<Word>();
		}
	}
	
	/**
	 * Used to replace an old word with a new (updates dependencies). 
	 * Used to make a predicate from a word during predicate identification.
	 * @param w The Word
	 */
	public Word(Word w) {
		this.idx=w.idx;
		this.Form=w.Form;
		this.Lemma=w.Lemma;
		this.POS=w.POS;
		this.Feats=w.Feats;
		this.Deprel=w.Deprel;
		this.head=w.head;
		this.headID=w.headID;
		this.children=w.children;
		this.mySentence=w.mySentence;
		this.isBOS=w.isBOS;
		//Then we have to update our children to make them point to this head rather than the old
		for(Word child:children)
			child.head=this;
		//And update our head's children to forget the old and add this one
		head.children.remove(w);
		head.children.add(this);
	}
	
	public Word(String[] CoNLL2009Columns,Sentence s,int idx){
		this(CoNLL2009Columns[1],CoNLL2009Columns[3],CoNLL2009Columns[5],CoNLL2009Columns[7],s,idx);
//		this.Form=CoNLL2009Columns[1];
//		this.Lemma=CoNLL2009Columns[3];
//		this.POS=CoNLL2009Columns[5];
//		this.Feats=CoNLL2009Columns[7];
//		this.mySentence=s;
//		children=new TreeSet<Word>(s.wordComparator);
		this.headID=Integer.parseInt(CoNLL2009Columns[9]);
		this.Deprel=CoNLL2009Columns[11];
		if(CoNLL2009Columns.length>=14){
			args=new String[CoNLL2009Columns.length-14];
			for(int i=0;i<args.length;++i){
				args[i]=CoNLL2009Columns[14+i];
			}
		}
	}

	/*
	 * Getters
	 */
	public String getAttr(WordData attr){
		switch(attr){
		case Form: return Form;
		case Lemma: return Lemma;
		case POS: return POS;
		case Deprel: return Deprel;
		default: throw new Error("You are wrong here."); //We shouldn't enter here
		}
	}
	public String getForm() {
		return Form;
	}
	public String getLemma() {
		return Lemma;
	}
	public String getPOS() {
		return POS;
	}
	public String getFeats() {
		return Feats;
	}
	public Word getHead() {
		return head;
	}
	public int getHeadId() {
		return headID;
	}
	public String getDeprel() {
		return Deprel;
	}
	public Word getPotentialArgument() {
		return potentialArgument;
	}
	public Set<Word> getChildren(){
		return children;
	}
	public Sentence getMySentence() {
		return mySentence;
	}
	public String getArg(int i){
		try {
			return args[i];
		} catch(ArrayIndexOutOfBoundsException e){
			System.err.println("Corpus contains errors, missing semantic arguments, Word: "+this);
			return "_";
		}
	}

	/*
	 * Setters
	 */
	public void setHead(Word h) {
		head=h;
		headID=mySentence.indexOf(h);
		h.children.add(this);
	}
	public void setDeprel(String deprel){
		this.Deprel=deprel;
	}
	public void setPotentialArgument(Word potentialArgument) {
		this.potentialArgument = potentialArgument;
	}
	protected void setChildren(HashSet<Word> children){
		this.children=children;
	}
	
	void clearArgArray(){
		args=null;
	}
	

	
	public boolean isBOS(){
		return isBOS;
	}
	public boolean isPassiveVoiceEng() {
		if(!getPOS().equals("VBN")) 
			return false;
		if(!head.isBOS && head.Form.matches("(be|am|are|is|was|were|been)"))
			return true;
		
		return false;
	}

	/*
	 * Getters for siblings and dependents
	 */
	public Word getLeftSibling() {
		for(int i=(mySentence.indexOf(this)-1);i>0;--i){
			if(head.children.contains(mySentence.get(i)))
				return mySentence.get(i);
		}
		return null;
	}
	
	public Word getRightSibling() {
		for(int i=(mySentence.indexOf(this)+1);i<mySentence.size();++i){
			if(head.children.contains(mySentence.get(i)))
				return mySentence.get(i);
		}
		return null;
	}
	public Word getRightmostDep(){
		if(children.isEmpty()) 
			return null;
		Word ret=null;
		for(int i=mySentence.indexOf(this);i<mySentence.size();++i){
			if(children.contains(mySentence.get(i)))
				ret=mySentence.get(i);
		}
		return ret;
	}
	public Word getLeftmostDep(){
		if(children.isEmpty()) 
			return null;
		Word ret=null;
		for(int i=mySentence.indexOf(this);i>0;--i){
			if(children.contains(mySentence.get(i)))
				ret=mySentence.get(i);
		}
		return ret;
	}	

	public static List<Word> findPath(Word pred,Word arg){
		List<Word> predPath=pathToRoot(pred);
		List<Word> argPath=pathToRoot(arg);
		List<Word> ret=new ArrayList<Word>();
		
		int commonIndex=0;
		int min=(predPath.size()<argPath.size()?predPath.size():argPath.size());
		for(int i=0;i<min;++i) {
			if(predPath.get(i)==argPath.get(i)){ //Always true at root (ie first index)
				commonIndex=i;
			}
		}
		for(int j=predPath.size()-1;j>=commonIndex;--j){
			ret.add(predPath.get(j));
		}
		for(int j=commonIndex+1;j<argPath.size();++j){
			ret.add(argPath.get(j));
		}
		return ret;
	}
	
	public static List<Word> pathToRoot(Word w){
		List<Word> path;
		if(w.isBOS){
			path=new ArrayList<Word>();
			path.add(w);
			return path;
		}
		path=pathToRoot(w.head);
		path.add(w);
		return path;
	}
	/**
	 * Converts this Word object one line following the CoNLL 2009 format.
	 * However, it does not include all columns, since this is part of a sentence.
	 * For proper CoNLL 2009 format output, use the Sentence.toString() method
	 */
	public String toString() {
		return Form+"\t"+Lemma+"\t"+Lemma+"\t"+POS+"\t"+POS+"\t_\t"+Feats+"\t"+headID+"\t"+headID+"\t"+Deprel+"\t"+Deprel;
		//return Form+"\t"+Lemma+"\t"+POS+"\t"+headID+"\t"+Deprel;
	}
	/**
	 * Recursive function that returns all nodes (words) dominated by the nodes,
	 * 
	 * @param words The nodes to descend from
	 * @return
	 */
	private static Collection<Word> getDominated(Collection<Word> words){
		Collection<Word> ret=new HashSet<Word>(words);
		for(Word c:words)
			ret.addAll(getDominated(c.getChildren()));
		return ret;
	}
	/**
	 * Returns the yield of this word, ie the complete phrase that defines the argument,
	 * with respect to the predicate. It follows algorithm 5.3 in Richard Johansson (2008), page 88
	 * @param pred The predicate of the proposition, required to deduce the yield
	 * @return the Yield
	 */
	public Yield getYield(Predicate pred,String argLabel,Set<Word> argSet){
		Yield ret=new Yield(pred,mySentence,argLabel);
		ret.add(this);
		if(pred==this) //If the predicate is the argument, we don't consider the yield
			return ret;
		for(Word child:children){
			if(!argSet.contains(child)){ //We don't branch down this child if 
				Collection<Word> subtree=getDominated(Arrays.asList(child));
				if(!subtree.contains(pred))
					ret.addAll(subtree);
			}
		}
		//ret.addAll(getDominated(children));
		return ret;
	}

	public int getIdx() {
		return idx;
	}
	
	public static final Comparator<Word> WORD_LINEAR_ORDER_COMPARATOR=new Comparator<Word>(){
		@Override
		public int compare(Word arg0, Word arg1) {
			return arg0.idx-arg1.idx;
		}
	};
}
