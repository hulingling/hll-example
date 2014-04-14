imDC是针对不平衡数据分类而提出的一个分类预测算法

该算法目前支持1）对训练数据交叉验证；2）对已有训练数据学习，然后对测试数据集进行预测。用户可以使用该软件包对自己的训练集进行交叉验证，以验证该算法是否对自己的数据高效，若该算法能够满足需要便可以对未知数据集进行预测。

文件自带的数据集包括train.arff和test.arff，train.arff为训练数据集，test.arff为待预测的数据集。

1，数据集交叉验证
java -jar imDC.jar train.arff -l 1 -c 5
java -jar imDC.jar train.arff -c 5
注意：其中train.arff为训练数据集位置；-l 1描述样本少数类类别，默认使用样本类别数最少的那个类别；-c 5描述交叉验证数，实例设置为五折交叉验证。

2，对待预测数据集进行预测
java -jar imDC.jar train.arff -l 1 -p test.arff result.txt
java -jar imDC.jar train.arff -p test.arff result.txt
注意：其中train.arff为训练数据集位置；-l 1描述样本少数类类别，默认使用样本类别数最少的那个类别；-p test.arff为待预测文件位置；result.txt为结果文件。


