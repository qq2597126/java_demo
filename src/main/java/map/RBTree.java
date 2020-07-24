package map;

import com.sun.org.apache.xpath.internal.objects.XNodeSet;

import java.util.HashMap;

/**
 * @author lcy
 * @DESC: 红黑树
 * @date 2020/7/17.
 */
public class RBTree<K extends Comparable,V> {

    private Node<K,V> root =  null;

    public Node<K,V> put(K key, V value){
        if(key == null){
            throw new RuntimeException("KEY IS NULL");
        }
        Node node = new Node(key,value);
        node.setPrev(getPrev(node));
        return insertNode(node);
    }



    private  Node<K,V> insertNode(Node<K,V> node){
        Node<K,V> addNode = node;
        //查询当前节点的父节点
        if(node.getPrev() == null){ // 1. 如果parent为空 表示当前节点为根节点。设置成黑色。并赋值.
            node.setRed(false);
            root = node;
            return  node;
        }else if(!node.getPrev().getRed()){
            return node;
        }else{
            //当前节点的父节点
            Node pNode = null;
            //当前节点的叔叔节点
            Node uncleNode = null;
            //当前节点的祖父节点
            Node ppNode = null;
            //循环处理
            for (;;){
                pNode = node.getPrev();

                ppNode = node.getPPNode();

                uncleNode = node.getUncleNode();


                if(uncleNode != null
                        && ppNode != null
                        && uncleNode.getRed() && pNode.getRed() // 2.1 父节点和叔叔节点为红色， pp设置为红色，p和u设置为黑色。 如果祖父为root节点。则直接为黑色(直接结束)
                ){
                    uncleNode.setRed(false);
                    pNode.setRed(false);
                    if(ppNode != root){
                        ppNode.setRed(true);
                        return node;
                    }
                    node = ppNode;
                }else if (
                        (uncleNode == null || !uncleNode.getRed()) //2.2叔叔节点空或者黑色,当前节点是父节点是祖父节点的左节点
                               && ppNode.getLeft() == pNode
                ){
                    if(pNode.getLeft() == node){ //2.2.1当前节点为父的左节点
                        pNode.setRed(false); // 1. P和S 图黑  2.pp图红 3.对PP右旋
                        ppNode.setRed(true);
                        rightRotate(ppNode);

                    }else if(pNode.getRight() == node) { // 2.2.2当前节点为右节点
                        leftRotate(pNode); // 1.P左旋。设置当前节点为P节点
                        node=pNode;
                    }
                }else{ //2.3叔叔节点不存在，或者为黑色， 父节点为左节点
                    if(pNode.getLeft() == node){ //2.3.1当前节点为父的左节点
                        pNode.setRed(false); // 1. P和S 图黑  2.pp图红 3.对PP左旋旋
                        ppNode.setRed(true);
                        leftRotate(ppNode);

                    }else if(pNode.getRight() == node) { // 当前节点为右节点
                        rightRotate(pNode);
                        node=pNode;
                    }
                }

                return node;
            }

        }

    }

    /**
     * 获取当前节点父节点
     * @param node
     * @return
     */
    public Node getPrev(Node node){
        if(node.getPrev() == null){
            // 查到当前父节点
            if(root == null){ //表示没有父节点
                return null;
            }else{
                Node prev = recursivePrev(node, root);
                return prev;
            }
        }
        return  node.getPrev();
    }

    /**
     * 递归查询当前父节点
     * @param node
     * @return
     */
    public Node recursivePrev(Node node,Node prev){
        if(node.getKey().compareTo(prev.getKey()) == 0){
            return prev;
        }else if(node.getKey().compareTo(prev.getKey()) >0){
            if(prev.getRight() != null){
                recursivePrev(node,prev.getRight());
            }else{
                prev.setRight(node);
                return prev;
            }
        }else {
            if(prev.getLeft() != null){
                recursivePrev(node,prev.getRight());
            }else{
                prev.setLeft(node);
                return prev;
            }
        }
        return  null;
    }

    // 当前节点右旋
    public void rightRotate(Node<K,V> node){
        Node<K,V> left = node.getLeft();
        Node<K,V> prev = node.getPrev();
        left.setPrev(prev);
        node.setLeft(left.getRight());
        left.setRight(node);
        if(root == node){
            root = left;
        }
    }
    //当前节点左旋
    public void leftRotate(Node<K,V> node){

        Node<K,V> right = node.getRight();
        Node<K,V> prev = node.getPrev();
        right.setPrev(prev);
        node.setRight(right.getLeft());
        right.setRight(node);
        if(root == node){
            root = right;
        }
    }
    // 当前节点右旋



    private class Node<K extends Comparable,V>{
        //private RBTree<K,V> parent;  // red-black tree links
        private Node<K,V> left;
        private Node<K,V> right;
        private Node<K,V> prev;    // needed to unlink next upon deletion

        private K key;
        private V value;
        private boolean red = true;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public Node(boolean red) {
            this.red = red;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public Node<K, V> getLeft() {
            return left;
        }

        public void setLeft(Node<K, V> left) {
            this.left = left;
        }

        public Node<K, V> getRight() {
            return right;
        }

        public void setRight(Node<K, V> right) {
            this.right = right;
        }

        public Node<K, V> getPrev() {
            return prev;
        }

        public void setPrev(Node<K, V> prev) {
            this.prev = prev;
        }

        public boolean getRed() {
            return red;
        }

        public void setRed(boolean red) {
            this.red = red;
        }
        //获取祖父节点
        public Node<K, V> getPPNode(){
            Node<K, V> ppNode = null;
            Node<K, V> pNode = this.getPrev();
            //设置祖父节点
            if(pNode != null){
                ppNode = pNode.getPrev();
            }
            return ppNode;
        }
        // 获取叔叔节点
        public Node<K, V> getUncleNode(){
            Node<K,V> uncleNode = null;
            Node<K, V> ppNode = getPPNode();
            // 设置
            if(ppNode != null && ppNode.getLeft() == getPrev()){
                uncleNode = ppNode.getRight();
            }else if(ppNode != null && ppNode.getRight() == getPrev()){
                uncleNode = ppNode.getLeft();
            }
            return uncleNode;
        }

    }

    @Override
    public String toString() {

        return super.toString();
    }
}
