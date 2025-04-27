#include <jni.h>
#include <string>
#include <sstream>
#include <iomanip> // <-- For nice decimal formatting
#include <Eigen/Dense>

using namespace Eigen;

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_matrixcalculator20_MainActivity_calculateMatrix(
        JNIEnv *env,
        jobject,
        jint op,
        jdoubleArray aArr,
        jint ra,
        jint ca,
        jdoubleArray bArr,
        jint rb,
        jint cb) {

    jsize lenA = env->GetArrayLength(aArr);
    jsize lenB = env->GetArrayLength(bArr);
    jdouble *aElems = env->GetDoubleArrayElements(aArr, 0);
    jdouble *bElems = env->GetDoubleArrayElements(bArr, 0);

    Map<Matrix<double, Dynamic, Dynamic, RowMajor>> A(aElems, ra, ca);
    Map<Matrix<double, Dynamic, Dynamic, RowMajor>> B(bElems, rb, cb);

    std::ostringstream oss;

    try {
        MatrixXd result;

        switch (op) {
            case 0:
                result = A + B;
                break;
            case 1:
                result = A - B;
                break;
            case 2:
                result = A * B;
                break;
            case 3:
                // Matrix division: A * (B inverse)
                if (rb != cb) {
                    oss << "Matrix B must be square for division!";
                    goto end;
                }
                if (B.determinant() == 0) {
                    oss << "Matrix B is not invertible!";
                    goto end;
                }
                result = A * B.inverse();
                break;
            default:
                oss << "Invalid operation";
                goto end;
        }

        // Format numbers nicely: fixed point, 2 decimal places
        oss << std::fixed << std::setprecision(2);

        for (int i = 0; i < result.rows(); ++i) {
            for (int j = 0; j < result.cols(); ++j) {
                oss << result(i, j);
                if (j != result.cols() - 1) oss << " "; // Separate numbers by space only
            }
            oss << "\n"; // Newline after each row
        }

    } catch (...) {
        oss << "Matrix operation failed!";
    }

    end:
    env->ReleaseDoubleArrayElements(aArr, aElems, JNI_ABORT);
    env->ReleaseDoubleArrayElements(bArr, bElems, JNI_ABORT);

    return env->NewStringUTF(oss.str().c_str());
}
