
public class ArrayUniquePriorityQueue<T> implements UniquePriorityQueueADT<T>{
    private T[] queue;
    private double[] priority;
    private int count;

    public ArrayUniquePriorityQueue () {
        count = 0;
        queue = (T[]) new Object[10];
        priority = new double[10];

    }
    @Override
    public void add (T data, double prio) {
        for (int i = 0; i < count; i++) {
            if (queue[i].equals(data)) {
                return;
            }
        }
        if (count == queue.length) {
            T[] newQueue = (T[]) new Object[queue.length + 5];
            double[] newPriority = new double[priority.length + 5];

            for (int x = 0; x < queue.length; x++) {
                newQueue[x] = queue[x];
                newPriority[x] = priority[x];
            }

            queue = newQueue;
            priority = newPriority;

        }
        int insertIndex = count;
        for (int z = 0; z < count; z++) {
            if (prio < priority[z]) {
                insertIndex = z;
                break;
            }
        }
        for (int p = count; p > insertIndex; p--) {
            queue[p] = queue[p - 1];
            priority[p] = priority[p - 1];
        }

        queue[insertIndex] = data;
        priority[insertIndex] = prio;

        count++;
    }


    @Override
    public boolean contains(T data) {
        // TODO Auto-generated method stub
        boolean result = false;
        for (int i = 0; i < count; i++) {
            if (queue[i].equals(data)) {
                result = true;
            }
        } return result;

    }

    @Override
    public T peek() throws CollectionException {
        if (isEmpty()) {
            throw new CollectionException("PQ is empty");
        }
        return queue[0];
    }

    @Override
    public T removeMin() throws CollectionException {
        if (isEmpty()) {
            throw new CollectionException("PQ is empty");
        }

        count = count - 1;

        T min = queue[0];

        for (int i = 0; i < count; i++) {
            queue[i] = queue[i+1];
            priority[i] = priority[i+1];
        }
        queue[count] = null;
        return min;
    }

    @Override
    public void updatePriority(T data, double newPrio) throws CollectionException {
        // TODO Auto-generated method stub

        boolean foundResult = false;
        int removeIndex = 0;

        for (int i = 0; i < count; i++) {
            if (queue[i].equals(data)) {

                removeIndex = i;
                foundResult = true;
            }
        }
        if (foundResult == false) {
            throw new CollectionException("Item not found in PQ");
        }
        for (int i = removeIndex; i < count - 1; i++) {
            queue[i] = queue[i + 1];
            priority[i] = priority[i + 1];
        }
        count--;

        add(data, newPrio);
    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        if (count == 0) {
            return true;
        }
        return false;
    }

    @Override
    public int size() {
        // TODO Auto-generated method stub
        return count;
    }

    public int getLength () {
        return priority.length;
    }

    public String toString () {
        String string = "";
        if (isEmpty()) {
            string = "The PQ is empty";
        }
        else {
            for (int i = 0; i<count; i++) {
                string += queue[i] + " [" + priority[i] +"]";
                if (i!=count-1) {
                    string += ", ";
                }
            }
        }

        return string;
    }
}
