
import java.util.Date;

public class MerkleNode {

    MerkleNode parent = null;
    Date date;
    int hash;
    int numOfChildren;
    MerkleNode [] children;

    public MerkleNode(Date randomDate, int value, int numOfChildren){
        date =  randomDate;
        hash = value;
        this.numOfChildren = numOfChildren;
        children = new MerkleNode [numOfChildren];
        for(int i=0; i<numOfChildren; i++) {
            this.children[i]= null;
        }
    }
    public MerkleNode(Date randomDate, int value, MerkleNode[] nodes, int numOfChildren){
        date =  randomDate;
        hash = value;
        this.numOfChildren = numOfChildren;
        children = new MerkleNode [numOfChildren];
        for(int i=0; i<numOfChildren; i++) {
            this.children[i]= nodes[i];
        }
    }
    public void setParent(MerkleNode parent){
        this.parent = parent;
    }

}

