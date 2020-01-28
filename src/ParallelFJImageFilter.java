import java.util.concurrent.RecursiveAction;

public class ParallelFJImageFilter extends RecursiveAction {
    private int[] mSource;
    private int[] mDestination;
    private int mStart;
    private int mLength;
    private int mWidth;
    private final int NRSTEPS = 100;
    // private int start;


    public ParallelFJImageFilter(int[] src, int[] dst, int start, int length, int width){

        this.mSource = src;
        this.mDestination = dst;
        mStart = start;
        mLength = length;
        mWidth = width;

    }
    protected void apply(){

        int index, pixel;
        for (int steps = 0; steps < NRSTEPS; steps++) {
            for (int i = mStart; i < mLength - 1; i++) {
                for (int j = 1; j < mWidth - 1; j++) {
                    float rt = 0, gt = 0, bt = 0;
                    for (int k = i - 1; k <= i + 1; k++) {
                        index = k * mWidth + j - 1;//n vend te width startin
                        pixel = mSource[index];
                        rt += (float) ((pixel & 0x00ff0000) >> 16);
                        gt += (float) ((pixel & 0x0000ff00) >> 8);
                        bt += (float) ((pixel & 0x000000ff));

                        index = k * mWidth + j;
                        pixel = mSource[index];
                        rt += (float) ((pixel & 0x00ff0000) >> 16);
                        gt += (float) ((pixel & 0x0000ff00) >> 8);
                        bt += (float) ((pixel & 0x000000ff));

                        index = k * mWidth + j + 1;
                        pixel = mSource[index];
                        rt += (float) ((pixel & 0x00ff0000) >> 16);
                        gt += (float) ((pixel & 0x0000ff00) >> 8);
                        bt += (float) ((pixel & 0x000000ff));
                    }
                    // Re-assemble destination pixel.
                    index = i * mWidth + j;
                    int dpixel = (0xff000000) | (((int) rt / 9) << 16) | (((int) gt / 9) << 8) | (((int) bt / 9));
                    mDestination[index] = dpixel;
                }
            }
            // swap references
            int[] help; help = mSource; mSource = mDestination; mDestination = help;
        }

    }
    protected static int nThreshold = 1000;


    @Override
    protected void compute() {
        if ((mLength - mStart) < nThreshold) {
            apply();
        }else {

        int mSplit = (mStart + mLength) / 2;

        invokeAll(new ParallelFJImageFilter(mSource, mDestination, mStart, mSplit, mWidth),
                new ParallelFJImageFilter(mSource, mDestination, mSplit , mLength, mWidth));
        }
    }
}

