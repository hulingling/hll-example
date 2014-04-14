
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;
import java.util.TreeMap;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.RBFNetwork;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IB1;
import weka.classifiers.lazy.IBk;
import weka.classifiers.rules.DTNB;
import weka.classifiers.rules.JRip;
import weka.classifiers.rules.OneR;
import weka.classifiers.rules.PART;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.ADTree;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.LMT;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;
import weka.classifiers.trees.SimpleCart;
import weka.core.Instance;
import weka.core.Instances;


public class imDC {

	private Instances m_instances = null;
	private int bestClassifierNum=5;//迭代分类器数量
	private Classifier[] bestClassifiers = new Classifier[bestClassifierNum];//选择bestClassifierNum个优秀的分类器
	private int lessLabelNum=0;//少数类的数目
	private double lessLabel = 0;//少数类的类别
	/**
	 * @param args
	 */
	public void getFileInstances(String fileName) throws Exception {
		FileReader frData = new FileReader(fileName);
		m_instances = new Instances(frData);
		m_instances.setClassIndex(m_instances.numAttributes() - 1);
	}
	public void getBestClassifier(String lessLabelParameter) throws Exception{
		int numInstances = m_instances.numInstances();
		int numAttributes=m_instances.numAttributes();//属性数目
	    Instances trainData = new Instances(m_instances, numInstances);//抽取少数类和同等数量的大类组成的数据集
	    
	    //start 获得少数类类别，数量等信息
	    System.out.println("获取最小类类别，实例数......");
	    double[] labelArray = m_instances.attributeToDoubleArray(numAttributes-1);
	    if(lessLabelParameter==null){
	    	Hashtable<Double, Integer> labelHashTable=new Hashtable();
		    double key=0;
		    int value=0;
		    for(int i=0;i<=labelArray.length - 1; i++) {
		    	key = labelArray[i];
		    	if(labelHashTable.containsKey(key)){
		    		value=labelHashTable.get(key)+1;
		    		labelHashTable.put(key, value);
		    	}else{
		    		labelHashTable.put(key, 1);
		    	}
		    }
		    Enumeration<Double> keys=labelHashTable.keys();
		    key=keys.nextElement();
		    lessLabelNum=labelHashTable.get(key);
		    while(keys.hasMoreElements()){
		    	key=keys.nextElement();
		    	if(labelHashTable.get(key)<lessLabelNum){
		    		lessLabelNum=labelHashTable.get(key);
		    		lessLabel=key;
		    	}
		    }
	    }else{
	    	lessLabel=Double.parseDouble(lessLabelParameter);
	    	for(int i=0;i<=labelArray.length - 1; i++) {
		    	if(labelArray[i]==Double.parseDouble(lessLabelParameter)){
		    		lessLabelNum++;
		    	}
		    }
	    }
	    
	    System.out.println("最小类类别为："+lessLabel+",数量为："+lessLabelNum);
	    //end
	    //start 先在trainData加入生成最小类，抽取同等数量的最大类加入trainData
	    System.out.println("准备测试最优分类器的抽取样本......");
	    Instances instanceMore = new Instances(m_instances, numInstances);
	    for(int i = numInstances - 1; i >= 0; i--) {
	    	if(m_instances.instance(i).classValue()==lessLabel){
	    		trainData.add(m_instances.instance(i));
	    	}
	    	else{
	    		instanceMore.add(m_instances.instance(i));
	    	}
	    }
	    Random r = new Random();
	    for(int i=0;i<lessLabelNum;i++){
	    	int randomNum=r.nextInt(instanceMore.numInstances()-1);
	    	trainData.add(instanceMore.instance(randomNum));
	    }
	    //end
	    //start 选择最优的5个分类器
	    System.out.println("从16个分类算法中获取适合样本的最优分类器......");
	    double[] accuracys = new double[bestClassifierNum];//5个优秀的分类器
	    double accuracy=0;
	    int tempNum=0;
	    int minIndex=0;
	    double minAccuracy=0;
	    Classifier[] classifiers = new Classifier[16];
	    classifiers[0]=new J48();
	    classifiers[1]=new J48();
	    classifiers[2]=new Logistic();
	    classifiers[3]=new SMO();
	    classifiers[4]=new J48();
	    //classifiers[4]=new Logistic();
	    classifiers[5]=new OneR();
	    classifiers[6]=new IBk();
	    classifiers[7]=new ZeroR();
	    classifiers[8]=new IBk();
	    //classifiers[8]=new DTNB();
	    classifiers[9]=new JRip();
	    classifiers[10]=new PART();
	    classifiers[11]=new RandomForest();
	    classifiers[12]=new J48();
	    classifiers[13]=new J48();
	    classifiers[14]=new REPTree();
	    classifiers[15]=new RandomTree();
	    for(int i=0;i<classifiers.length;i++){
	    	System.out.print("测试第"+i+"个:");
	    	Evaluation eval = new Evaluation(trainData);
	    	eval.crossValidateModel(classifiers[i], trainData,5, new Random(1));
	    	accuracy=1-eval.errorRate();
	    	System.out.println(classifiers[i].getClass().getSimpleName()+":"+accuracy);
	    	if(tempNum<bestClassifierNum){
	    		accuracys[tempNum]=accuracy;
    			bestClassifiers[tempNum]=classifiers[i];
    			tempNum++;
	    	}else{
	    		minAccuracy=accuracys[0];
	    		for(int j=0;j<accuracys.length;j++){
		    		if(accuracys[j]-minAccuracy<= 0.000000000000001){
		    			minAccuracy=accuracys[j];
		    			minIndex=j;
		    		}
		    	}
	    		if(minAccuracy-accuracy <= 0.000000000000001){
	    			accuracys[minIndex]=accuracy;
		    		bestClassifiers[minIndex]=classifiers[i];
	    		}
	    	}
	    }
	    System.out.print("最优分类算法为：");
	    for(int i=0;i<bestClassifiers.length;i++){
	    	System.out.print(bestClassifiers[i].getClass().getSimpleName()+" ");
	    }
	    System.out.println();
	    //end
	}
	public static double query(double[] preres)
	{
		double max=0;
		int index=0;
		
		for(int i=0;i<preres.length;i++){
			if(preres[i]>max){
				max=preres[i];
				index=i;
			}
		}
		return index;
	}
	public static void main(String[] args) throws Exception {
		//TrainFilePath -l 1 -c cvNum or TrainFilePath -l 1 -p TestFilePath resultFilePath
		/*int length = args.length;
		String TrainFilePath = null,TestFilePath = null,cvNum = null,lessLabelParameter = null,resultFilePath = null;
		String flag;
		TrainFilePath=args[0];
		if(length==3 || length==4){
			if(args[1].equals("-c")){
				cvNum=args[2];
				flag="c";
			}
			else{
				TestFilePath=args[2];
				resultFilePath=args[3];
				flag="p";
			}
		}else{
			lessLabelParameter = args[2];
			if(!lessLabelParameter.matches( "\\d+\\.?\\d*")){
				System.out.println("最小类类别格式不正确");
				return;
			}
			if(args[3].equals("-c")){
				cvNum=args[4];
				flag="c";
			}
			else{
				TestFilePath=args[4];
				resultFilePath=args[5];
				flag="p";
			}
		}
		*/
		String TrainFilePath="C:/Users/ysxin/Desktop/imDC/train111.arff";
		String cvNum="5";
		String TestFilePath = "C:/Users/ysxin/Desktop/imDC/Indep_188.arff",
				resultFilePath = "C:/Users/ysxin/Desktop/imDC/result.txt";
		String lessLabelParameter = "0";
		String flag="c";
		
		imDC imdc = new imDC();
		imdc.getFileInstances(TrainFilePath);
		imdc.getBestClassifier(lessLabelParameter);
		myClassifier1 myclassifier=new myClassifier1(imdc.m_instances,imdc.bestClassifiers,imdc.lessLabelNum,imdc.lessLabel,imdc.bestClassifierNum);
		System.out.println("初始化集成分类算法（包括初始权重，迭代分类器设置）......");
		myclassifier.initmyclassifier();
		
		//交叉验证
		if(flag.equals("c")){
			System.out.println("开始预测样本......");
			Evaluation eval = new Evaluation(imdc.m_instances);
			eval.crossValidateModel(myclassifier, imdc.m_instances,Integer.parseInt(cvNum), new Random(1));
			System.out.println(eval.toSummaryString());
			System.out.println(eval.toClassDetailsString());
			System.out.println(eval.toMatrixString());
		}else if(flag.equals("p")){
			//测试集结果预测
			myclassifier.buildClassifier(imdc.m_instances);
			FileReader frData = new FileReader(TestFilePath);
			Instances test_instances = new Instances(frData);
			test_instances.setClassIndex(test_instances.numAttributes() - 1);
			double result;
			System.out.println("开始预测样本......");
			//输出结果加编号，没用的可以删除
			int num=1;
			File f = new File(resultFilePath);
		    FileWriter fw = new FileWriter(f);
		    BufferedWriter bw = new BufferedWriter(fw);
			for(int i=0;i<test_instances.numInstances();i++){
				result=query(myclassifier.distributionForInstance(test_instances.instance(i)));
			    bw.write(num+","+result);
			    num++;
			    bw.newLine();
			}
			bw.close();
		}
		
		
		//使用测试集验证,当测试集有类别时验证算法性能
		/*myclassifier.buildClassifier(imdc.m_instances);
		FileReader frData = new FileReader(TestFilePath);
		Instances test_instances = new Instances(frData);
		test_instances.setClassIndex(test_instances.numAttributes() - 1);
		double result;
		int TP=0,FN=0,TN=0,FP=0;
		System.out.println("开始预测样本......");
		for(int i=0;i<test_instances.numInstances();i++){
			result=query(myclassifier.distributionForInstance(test_instances.instance(i)));
			if(test_instances.instance(i).classValue()==imdc.lessLabel){
				if(result==test_instances.instance(i).classValue()){//少数and正确,TP
					TP++;
				}else{//少数and错误,FN
					FN++;
				}
			}else{
				if(result==test_instances.instance(i).classValue()){//多数and正确,TN
					TN++;
				}else{//多数and错误,FP
					FP++;
				}
			}
		}
		System.out.println(TN+" "+FP);
		System.out.println(FN+" "+TP);
		System.out.println("sn:"+1.0*TP/(TP+FN));
		System.out.println("sp:"+1.0*TN/(FP+TN));
		*/
		
		
	}
	

}
