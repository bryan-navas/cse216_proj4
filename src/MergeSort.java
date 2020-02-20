import java.util.Random;
import java.util.stream.IntStream;

public class MergeSort {

    private static final Random RNG    = new Random(10982755L);
    private static final int    LENGTH = 500000;
    private static final int THREAD_NUM = 1;       //THIS VARIABLE IS USED TO CHANGE THE NUMBER OF THREADS

    public static void main(String... args) {
        int[] arr = randomIntArray();
        long start = System.currentTimeMillis();
        concurrentMergeSort(arr);
        long end = System.currentTimeMillis();
        if (!sorted(arr)) {
            System.err.println("The final array is not sorted");
            System.exit(0);
        }
        System.out.printf("%10d numbers: %6d ms%n", LENGTH, end - start);
    }

    private static int[] randomIntArray() {
        int[] arr = new int[LENGTH];
        for (int i = 0; i < arr.length; i++)
            arr[i] = RNG.nextInt(LENGTH * 10);
        return arr;
    }

    public static boolean sorted(int[] arr) {
        for (int i = 0; i < arr.length-1; i++){
            if(arr[i] > arr[i+1]){
                return false;
            }
        }
        return true;
    }

    public static void concurrentMergeSort(int[] arr){
            concurrentMergeSort(arr, THREAD_NUM);
    }

    public static void concurrentMergeSort(int[] arr, int threadCount){
        if(threadCount <= 1){
            sort(arr, 0, arr.length-1);  //if no more threads can be made/only one thread is allowed, then do regular MergeSort
        }
        else{

            //(new Thread(new Sorting(arr, threadCount))).start();
            //concurrentMergeSort(arr, threadCount/2);  //sort first half of the array but first split it IN a thread
            int middle = (arr.length)/2;
            int[] left = new int[middle+1];
            int[] right = new int[(arr.length-1) - middle];

            for(int i = 0; i < (middle+1); i++){
                left[i] = arr[i];
            }
            for(int j = 0; j < (arr.length-1)-middle; j++){
                right[j] = arr[middle + 1 + j];
            }

            Thread leftSort = new Thread(new Sorting(left, threadCount/2));
            leftSort.start();
            Thread rightSort = new Thread(new Sorting(right, threadCount/2));
            rightSort.start();

            try{
                leftSort.join();
                rightSort.join();
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            merge(arr, left, right);
        }
    }

    private static void sort(int[] data, int start, int end){
           if(start == end)   //recursive base case
               return;
           else{
               int middle = (start + end)/2;
               sort(data, start, middle);
               sort(data, middle+1, end);
               merge(data, start, middle, end);
           }

    }

    private static void merge(int[] data, int start, int middle, int end){
                int[] leftData = new int[middle-start +1];
                int[] rightData = new int[end-middle];
                //Copying data from data[] to left[] and right[]
                int leftIndex;
                int rightIndex;
                int leftLength = middle-start +1;
                int rightLength = end-middle;
                for(leftIndex = 0; leftIndex < leftLength; leftIndex++){
                    leftData[leftIndex] = data[start + leftIndex];
                }
                for(rightIndex = 0; rightIndex < rightLength; rightIndex++){
                    rightData[rightIndex] = data[middle + 1 + rightIndex];
                }
                int dataIndex = start;  //the beginning of the original index
                for(leftIndex = 0, rightIndex = 0;leftIndex < leftLength && rightIndex < rightLength; dataIndex++ ){
                    if(leftData[leftIndex] > rightData[rightIndex]){
                        data[dataIndex] = rightData[rightIndex];
                        rightIndex++;
                    }
                    else
                    {
                        data[dataIndex] = leftData[leftIndex];
                        leftIndex++;
                    }
                }
                while (leftIndex < leftLength){     //for remaining
                    data[dataIndex++] = leftData[leftIndex++];
                }
                while(rightIndex < rightLength){    //for remaining
                    data[dataIndex++] = rightData[rightIndex++];
                }
    }

    private static void merge(int[] data, int[] left, int [] right){
            int dataIndx = 0, leftIndx = 0, rightIndx = 0;
            while(leftIndx < left.length && rightIndx < right.length){
                if(left[leftIndx] <= right[rightIndx]){
                    data[dataIndx] = left[leftIndx];
                    leftIndx++;
                }
                else{
                    data[dataIndx] = right[rightIndx];
                    rightIndx++;
                }
                dataIndx++;
            }

            while (leftIndx < left.length){     //for remaining
                data[dataIndx++] = left[leftIndx++];
            }
            while(rightIndx < right.length){    //for remaining
                data[dataIndx++] = right[rightIndx++];
            }
    }

}