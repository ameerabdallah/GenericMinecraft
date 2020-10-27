package genericminecraft;

/***************************************************************
* @file: Block.java
* @author: Ameer Abdallah
* @class: CS 4450 - Computer Graphics
* 
* @assignment: Checkpoint 2
* @date last modified: 10/26/2020
*
* @purpose: 
* Holds block information
****************************************************************/ 
public class Block 
{
    private BlockType blockType; // whatever kind of block it is
    
    // method: Block
    // given no parameters assume the block that is given is air
    // construct a Block object
    public Block()
    {
        this.blockType = BlockType.AIR;
    }

    // constructor overload
    public Block(BlockType blockType)
    {
        this.blockType = blockType;
    }
    
    // Enum of all the different types of blocks
    public enum BlockType 
    {
        AIR(0),
        GRASS(1),
        DIRT(2),
        SAND(3),
        STONE(4),
        BEDROCK(5),
        WATER(6),
        GRAVEL(7),
        WOOD_LOG(8);

        private int BlockID;

        BlockType(int i)
        {
            BlockID = i;
        }

        public int GetID()
        {
            return BlockID;
        }
        public void SetID(int i)
        {
            BlockID = i;
        }
    }
    
    // return the block's id
    public int GetID()
    {
        return blockType.GetID();
    }
    
    // return the block type
    public BlockType getType()
    {
        return blockType;
    }
}
