import breeze.linalg._
import breeze.numerics.abs

import java.io._

object Main {
  def main(args: Array[String]): Unit = {
    var df = csvread(new File(args(0)), ';', skipLines = 1)
    var idx = (df.rows * 0.8).toInt // 80 на трейн и 20 процентов на валидацию
    var df_train = df(0 until idx, ::)
    var df_val = df(idx until df.rows, ::)
    print(df_train)
    var X_train = df_train(::, 0 until df_train.cols - 1)
    var X_val = df_val(::, 0 until df_val.cols - 1)
    var y_train = df_train(::, df_train.cols - 1)
    var y_val = df_val(::, df_val.cols - 1)
    var w = inv(X_train.t * X_train) * X_train.t * y_train
    var pred = X_val * w
    var MAE = sum(abs(pred - y_val)) / y_val.size
    println("MAE on validation data = " + MAE.toString)
    csvwrite(new File("result_val.csv"), pred.toDenseMatrix, separator = '\n')

    df = csvread(new File(args(1)), ';', skipLines = 1)
    var X_test = df(::, 0 to df.cols - 2)
    var y_test = df(::, df.cols - 1)
    pred = X_test * w
    MAE = sum(abs(pred - y_test)) / y_test.size
    println("MAE on test data = " + MAE.toString)
    csvwrite(new File("result_test.csv"), pred.toDenseMatrix, separator = '\n')
  }
}