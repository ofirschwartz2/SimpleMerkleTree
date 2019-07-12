import java.util.Date;
import java.util.Random;

public class Main {
    static int numOfChildren = 3;
    static int numOfDates = 16;

    public static void printTheMerkleTree(MerkleNode tree, String prefix) {
        if(tree == null ){
            return;
        }
        System.out.println(prefix + "├── " + tree.hash);
        for (int i = 0; i < numOfChildren; i++) {
            printTheMerkleTree(tree.children[i], prefix + "│   ");
        }
    }

    public static void printDates(MerkleNode[] merkleLeafs) {
        for(int i=0; i<numOfDates; i++) {
            System.out.print(merkleLeafs[i].date + "\t");
        }
    }

    public static Date generateRandomDate() {
        Random rand = new Random();
        return (new Date(118, rand.nextInt(12) + 1, rand.nextInt(32) + 1, rand.nextInt(24), rand.nextInt(60), rand.nextInt(60)));
    }

    public static MerkleNode generateMerkleTree(MerkleNode[] merkleLeafs) {
        MerkleNode merkleNode = null;
        int numOfNodesInLevel = 1;
        while (numOfNodesInLevel < numOfDates) {
            numOfNodesInLevel = numOfNodesInLevel * numOfChildren;
        }
        MerkleNode[] merkleNodesInLevel = new MerkleNode[numOfNodesInLevel];

        while (numOfNodesInLevel != 0) {
            for(int i=0; i< numOfNodesInLevel; i++ ) {
                if (numOfNodesInLevel >= numOfDates) { // fix the leaf level to be a full level (when (numOfNodesInLevel == 27))
                    if( i < numOfDates ) {
                        merkleNodesInLevel[i] = merkleLeafs[i];
                    } else {
                        merkleNodesInLevel[i] = merkleNodesInLevel[i-1];
                    }
                } else { // create father MerkleNode for every level but the leaf level.
                    int parentHash=0;
                    MerkleNode[] parentsChildren = new MerkleNode[numOfChildren];
                    for(int j=0; j<numOfChildren; j++) {
                        parentHash = parentHash + merkleNodesInLevel[(numOfChildren*i)+j].hash;
                        parentsChildren[j] = merkleNodesInLevel[(numOfChildren*i)+j];
                    }
                    merkleNode = new MerkleNode(null , Integer.hashCode(parentHash), parentsChildren, numOfChildren);
                    for (int k=0; k<numOfChildren; k++) {
                        merkleNodesInLevel[(numOfChildren*i)+k].setParent(merkleNode);
                    }
                    merkleNodesInLevel[i] = merkleNode;
                }
            }
            numOfNodesInLevel  = numOfNodesInLevel  / numOfChildren; // climb a level up the tree
        }
        return merkleNodesInLevel[0]; // return the root
    }

    public static int[] proveAlicesDateIncluded(MerkleNode bobsTree, MerkleNode[] merkleLeafs, Date alicesDate) {
        MerkleNode alicesRout = null;
        MerkleNode alicesRoutParent = null;
        int numOfNodes = 1;
        int ansLength = 0;
        int skip=0;
        while (numOfNodes < numOfDates) { // just to set the length of the answer (ret)
            numOfNodes = numOfNodes * numOfChildren;
            ansLength = ansLength + (numOfChildren - 1);
        }
        int[] ret = new int[ansLength];
        int retIndex=0;

        // find Alice's date in the tree
        for(int i=0; i<numOfDates; i++) {
            if(merkleLeafs[i].date == alicesDate) {
                alicesRout = merkleLeafs[i];
                break;
            }
        }
        // climb up the tree and build the proof for alice (ret)
        while (alicesRout.parent != null) {
            alicesRoutParent = alicesRout.parent;
            for (int i=0; i<numOfChildren; i++) {
                if (alicesRoutParent.children[i].hash == alicesRout.hash) {
                    skip = i;
                    break;
                }
            }
            for (int i=0; i<numOfChildren; i++) {
                if (i != skip) {
                    ret[retIndex] = alicesRoutParent.children[i].hash;
                    retIndex++;
                }
            }
            alicesRout = alicesRout.parent;
        }
        return ret;
    }
    public static boolean aliceChecksProof(int rootHash, int[] proof, Date alicesDate) {
        Integer alicesHash = alicesDate.hashCode();
        for (int i=0; i<proof.length; i++) {
            alicesHash = alicesHash + proof[i];
            if ( i%(numOfChildren - 1) == (numOfChildren - 2) ) { // rehash after adding 2 hash siblings (ternary)
                alicesHash = alicesHash.hashCode();
            }
        }
        return (rootHash == alicesHash);
    }

    //1. Using either language of your choice, we are only asking you to generate the code, in
    //its simplest form (i.e., it’s not necessary, but definitely possible, to implement a generic
    //Merkle Tree), to populate the tree and return the merkle root. To populate the tree, you
    //will randomly pick 16 dates from the year 2018. The only libraries that you should need
    //are hashing, random, and date libraries. Alongside the code, please attach a
    //printout  of the dates and the tree.
    //
    //2. Imagine there are two parties: Alice and Bob. Bob has the full Merkle Tree generated in
    //Section #1 (i.e., the output from running the code above), and Alice only has the Merkle
    //Tree’s root (defined as R) and one of the dates used in building it (pick any one of them).
    //Now, further imagine that you are Bob - and you need to convince Alice that her date is
    //indeed a part of the tree with root R. How will you convince her? Supply a minimal list
    //of hashes that prove that (this is also known as a Merkle Proof).
    //
    //3. Bonus questions: For a large number of elements in a merkle tree, is the proof using a
    //ternary tree shorter or longer than the proof using a binary Merkle tree? Please explain.
    //
    //3. Answer:
    // Ternary Merkle Tree: 2 siblings times the height of the tree -> 2*log3(n)
    // Binary Merkle Tree: 1 sibling times the height of the tree -> 1*log2(n)
    // from log rules: 2*log3(n) = 2*(log2(n)/log2(3))
    // so, for n>1 we know that log2(n)>0.
    // 2*log3(n)/log2(n) = 2*(log2(n)/log2(3))/log2(n) =  2/log2(3) > 2/2 = 1 = 1*log2(n)/log2(n) [*log2(n) for both sides]
    // for every n>1: 2*log3(n) > 1*log2(n)
    // Binary Merkle Trees have a shorter proof for every n>1.
    public static void main (String[] args) {
        if( (numOfChildren < 2) || (numOfDates<1) ){
            System.out.print("incorrect variables");
            return;
        }
        Random rand = new Random();
        Date[] randomDates = new Date[numOfDates];
        MerkleNode[] merkleLeafs = new MerkleNode[numOfDates];

        //Creation of the 16 random dates and the Merkle leafs
        for (int i = 0; i < numOfDates; i++) {
            randomDates[i] = generateRandomDate();
            merkleLeafs[i] = new MerkleNode(randomDates[i], randomDates[i].hashCode(), numOfChildren);
        }
        //Create Bob's Merkle tree:
        MerkleNode bobsTree = generateMerkleTree(merkleLeafs);

        //print the dates and Bob's tree:
        System.out.println("\nThe Random Dates:");
        printDates(merkleLeafs);
        System.out.println("\n\nBob's Tree:");
        printTheMerkleTree(bobsTree, "");

        //Alice's Date and Root hash:
        int alicesDateIndex = rand.nextInt(numOfDates);
        Date alicesDate = randomDates[alicesDateIndex];
        int alicesRootHash = bobsTree.hash;

        //Bob generates the proof for Alice:
        int[] proof = proveAlicesDateIncluded(bobsTree, merkleLeafs, alicesDate);

        System.out.println("\nAlice's Date is: " + alicesDate);
        System.out.println("Alice's Hash is: " + alicesDate.hashCode());
        System.out.print("Bob's proof: ");
        for (int i=0; i<proof.length; i++) System.out.print(proof[i] + " ");

        //Alice is making sure that her date is in the tree:
        if ( aliceChecksProof(alicesRootHash, proof, alicesDate) ) {
            System.out.println("\n\nAlice: My date is in the tree!");
        } else {
            System.out.println("\nAlice: My date is NOT in the tree!");
        }
    }
}
