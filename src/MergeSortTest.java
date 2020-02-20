import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MergeSortTest {

    @Test
    void testSorted(){
        int[] decreasingArr = {10,9,8,7,6,5,4,3,2,1,0,-1};
        int[] unsortedArr = {12,23,52,12,12,0,18};
        int[] nullArr = null;            //boundary value
        int[] simpleSortedArr = {-1,99};
        int[] oneElemArr = {-124};
        int[] emptyArr = new int[0];     //boundary value
        assertAll(
                () -> assertFalse(MergeSort.sorted(decreasingArr)),
                () -> assertFalse(MergeSort.sorted(unsortedArr)),
                () -> assertThrows(NullPointerException.class, () -> MergeSort.sorted(nullArr)),
                () -> assertTrue(MergeSort.sorted(simpleSortedArr)),
                () -> assertTrue(MergeSort.sorted(oneElemArr)),
                () -> assertTrue(MergeSort.sorted(emptyArr))
        );
    }
}