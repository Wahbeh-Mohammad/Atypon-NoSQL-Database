package com.atypon.read_controller.cache;

import org.json.JSONObject;

import java.util.concurrent.ConcurrentHashMap;

public class Cache implements AbstractCache<String, JSONObject> {
    private final int DEFAULT_CAPACITY = 25;
    private final ConcurrentHashMap<String, Node> nodeMapping;
    private final ConcurrentHashMap<Integer, DoubleLinkedList> frequencyMap;
    private int minimumFrequency;
    private int size;

    // Singleton Instance
    private static volatile Cache INSTANCE = null;

    private Cache() {
        nodeMapping = new ConcurrentHashMap<>(DEFAULT_CAPACITY);
        frequencyMap = new ConcurrentHashMap<>();
        minimumFrequency = 1;
        size = 0;
    }

    public static Cache getInstance() {
        if(INSTANCE == null) {
            synchronized (Cache.class) {
                if(INSTANCE == null) {
                    INSTANCE = new Cache();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public synchronized JSONObject get(String key) {
        Node node = nodeMapping.get(key);
        // miss
        if(node == null) {
            System.out.println("Cache miss");
            return null;
        }
        // hit
        update(node);
        System.out.println("Cache hit");
        return node.value;
    }

    @Override
    public synchronized void put(String key, JSONObject value) {
        if(nodeMapping.get(key) != null) {
            // this is regarded as a hit.
            // cache already has the key, update the value.
            Node node = nodeMapping.get(key);
            node.value = value;
            node.incrementFrequency();
            update(node);
        } else {
            if(size == DEFAULT_CAPACITY) {
                // evict lfu node
                DoubleLinkedList minimumFrequencyList = frequencyMap.get(minimumFrequency);
                Node lfuNode = minimumFrequencyList.deleteLast();
                if(lfuNode != null) {
                    nodeMapping.remove(lfuNode.key);
                    size--;
                }
            }
            // update minimum frequency to 1 since this is a new entry in the cache.
            minimumFrequency = 1;
            Node newNode = new Node(key, value);
            // add new node to the mapping
            nodeMapping.put(key, newNode);
            // check if there is a frequency list dedicated to new frequency, if not create one.
            if( !frequencyMap.containsKey(1))
                frequencyMap.put(1, new DoubleLinkedList());
            DoubleLinkedList lowestPossibleFrequencyList = frequencyMap.get(1);
            // add new node to the frequency list.
            lowestPossibleFrequencyList.add(newNode);
            size++;
        }
    }

    private void update(Node node) {
        // remove node from old frequency list (DoubleLinkedList)
        DoubleLinkedList oldFrequencyDLL = frequencyMap.get(node.frequency);
        oldFrequencyDLL.delete(node);

        // maintain least frequency and update node's frequency.
        if(minimumFrequency == node.frequency && oldFrequencyDLL.isEmpty() ) {
            minimumFrequency++;
        }

        node.incrementFrequency();

        // check if there is a frequency list dedicated to new frequency, if not create one.
        if( !frequencyMap.containsKey(node.frequency))
            frequencyMap.put(node.frequency, new DoubleLinkedList());

        // add node to frequency list
        DoubleLinkedList newFrequencyDLL = frequencyMap.get(node.frequency);
        newFrequencyDLL.add(node);
    }

    public synchronized void refreshCache() {
        nodeMapping.clear();
        frequencyMap.clear();
        minimumFrequency = 1;
        size = 0;
        System.out.println("Cache refreshed");
    }

    private static class Node {
        Node next, prev;
        String key;
        JSONObject value;
        int frequency;

        public Node() { // this constructor is used for both the head and the tail of the dll.
            this.next = null;
            this.prev = null;
            this.key = null;
            this.value = null;
            this.frequency = 1;
        }

        public Node(String key, JSONObject value) {
            this.key = key;
            this.value = value;
            this.frequency = 1;
        }

        public void incrementFrequency() {
            this.frequency = (this.frequency + 1)%((int)1e9 + 7);
        }
    }

    private static class DoubleLinkedList {
        Node head, tail;
        int size;

        public DoubleLinkedList() {
            // head & tail nodes are disregarded, meaning they have no information.
            head = new Node();
            tail = new Node();
            head.next = tail;
            tail.prev = head;
            size = 0;
        }

        public boolean isEmpty() {
            return size == 0;
        }

        public void add(Node node) {
            if(node == null)
                throw new IllegalArgumentException("Node to delete cannot be null.");
            head.next.prev = node;
            node.next = head.next;
            head.next = node;
            node.prev = head;
            size++;
        }

        public void delete(Node node) {
            if(node == null)
                throw new IllegalArgumentException("Node to delete cannot be null.");
            node.prev.next = node.next;
            node.next.prev = node.prev;
            size--;
        }

        public Node deleteLast() {
            if (!isEmpty()) {
                Node lfuNode = tail.prev;
                delete(lfuNode);
                return lfuNode;
            }
            return null;
        }
    }
}
