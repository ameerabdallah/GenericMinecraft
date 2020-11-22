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
    private Block top, bot, front, back, left, right;
    
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
        top = null;
        bot = null;
        front = null;
        back = null;
        left = null;
        right = null;
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

    public Block getTop() {
        return top;
    }

    public void setTop(Block top) {
        this.top = top;
    }

    public Block getBot() {
        return bot;
    }

    public void setBot(Block bot) {
        this.bot = bot;
    }

    public Block getFront() {
        return front;
    }

    public void setFront(Block front) {
        this.front = front;
    }

    public Block getBack() {
        return back;
    }

    public void setBack(Block back) {
        this.back = back;
    }

    public Block getLeft() {
        return left;
    }

    public void setLeft(Block left) {
        this.left = left;
    }

    public Block getRight() {
        return right;
    }

    public void setRight(Block right) {
        this.right = right;
    }
    
    public boolean drawTop()
    {
        return top == null 
                || top.getType() == Block.BlockType.AIR 
                || (blockType != Block.BlockType.WATER && top.getType() == Block.BlockType.WATER);
    }
    
    public boolean drawBot()
    {
        return bot == null 
                || bot.getType() == Block.BlockType.AIR 
                || (blockType != Block.BlockType.WATER && bot.getType() == Block.BlockType.WATER);
    }
    public boolean drawFront()
    {
        return front == null 
                || front.getType() == Block.BlockType.AIR 
                || (blockType != Block.BlockType.WATER && front.getType() == Block.BlockType.WATER);
    }
    public boolean drawBack()
    {
        return back == null 
                || back.getType() == Block.BlockType.AIR 
                || (blockType != Block.BlockType.WATER && back.getType() == Block.BlockType.WATER);
    }
    public boolean drawLeft()
    {
        return left == null 
                || left.getType() == Block.BlockType.AIR 
                || (blockType != Block.BlockType.WATER && left.getType() == Block.BlockType.WATER);
    }
    public boolean drawRight()
    {
        return right == null 
                || right.getType() == Block.BlockType.AIR 
                || (blockType != Block.BlockType.WATER && right.getType() == Block.BlockType.WATER);
    }
    
}
