import javax.swing.*
import java.awt.*
import java.lang.Exception
import java.lang.Math.abs
import java.util.*
import kotlin.collections.ArrayList

class Maze {
    val MWIDTH: Int = 100 ; val MHEIGHT: Int = 5
    val BLOCK: Int = 10
    private var robotActive: Boolean = false
    private val SPEED: Int = 30

    lateinit var mazecomp: MazeComponent
    private val LEFT: Int = 4; private val RIGHT: Int = 8;
    private val UP: Int = 1; private val DOWN: Int = 2

    private var robotX:Int = 0; private var robotY:Int = 0

    private lateinit var maze: Array<Array<Int>>
    private lateinit var crumbs: Array<Array<Boolean>>

    fun main(){
        maze = Array(MWIDTH) { Array(MHEIGHT) {0} }
        crumbs = Array(MWIDTH) {Array(MHEIGHT) {false} }
        for (i in 0 until MWIDTH){
            for (j in 0 until MHEIGHT){
                maze[i][j] = 31
                crumbs[i][j] = false
            }
        }
        makeMaze()
        //Knock down walls
        for(i in 0 until 201){
            val x:Int = (Math.random()*(MWIDTH-2)+1).toInt()
            val y:Int = (Math.random()*(MHEIGHT-2)+1).toInt()
            if(maze[x][y].and(LEFT)>0){
                maze[x][y] = maze[x][y].xor(LEFT)
                maze[x-1][y] = maze[x-1][y].xor(RIGHT)
            }
        }

        val f: JFrame = JFrame()
        val width = MWIDTH*BLOCK+30
        val height = MHEIGHT*BLOCK+45
        f.title = "MAZE!!! !(>3<)/"
        f.setSize(width, height)
        f.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        mazecomp = MazeComponent()
        f.add(mazecomp)
        f.isVisible = true
        //dfs and A* are here
        robotActive = true
        val dfsPath= dfs(maze).reversed() as ArrayList<Int>
        val astarPath = astar(maze).reversed() as ArrayList<Int>
        doMazeGuide(astarPath)
	println("Total Moves in A STAR SEARCH: "+ astarPath.size.toString())
        robotX = 0; robotY = 0
        doMazeGuide(dfsPath)
	println("Total Moves in DFS: "+ dfsPath.size.toString())
    }
    private fun makeMaze(){

        val blockListX: Array<Int> = Array(MWIDTH*MHEIGHT) {0}
        val blockListY: Array<Int> = Array(MWIDTH*MHEIGHT) {0}
        var blocks = 0
        var x:Int ; var y:Int
        //Choose random starting block and add it to maze
        x = (Math.random()*(MWIDTH-2)+1).toInt()
        y = (Math.random()*(MHEIGHT-2)+1).toInt()
        maze[x][y] -= 16
        //Add all adjacent blocks to blocklist
        if (x > 0){
            blockListX[blocks] = x - 1
            blockListY[blocks] = y
            blocks++
        }
        if (x < MWIDTH-1){
            blockListX[blocks] = x + 1
            blockListY[blocks] = y
            blocks++
        }
        if(y > 0){
            blockListX[blocks] = x
            blockListY[blocks] = y - 1
            blocks++
        }
        if(y < MHEIGHT-1){
            blockListX[blocks] = x
            blockListY[blocks] = y + 1
            blocks++
        }

        //approach:
        // start with a single room in maze and all neighbors of the room in the "blocklist"
        // choose a room that is not yet part of the maze but is adjacent to the maze
        // add it to the maze by breaking a wall
        // put all of its neighbors that aren't in the maze into the "blocklist"
        // repeat until everybody is in the maze

        while(blocks>0){

            //choosing random position to break the wall
            val b:Int = (Math.random()*blocks).toInt()

            //push the coordinate to the stack
            x = blockListX[b]
            y = blockListY[b]

            val dir:Array<Int> = Array(4) {0}
            var numdir:Int = 0

            //Choosing random wall to tear down the wall
            if(x>0 && (maze[x-1][y].and(16))==0)
                dir[numdir++] = 0
            if(x<MWIDTH-1 && (maze[x+1][y].and(16))==0)
                dir[numdir++] = 1
            if(y>0 && (maze[x][y-1].and(16))==0)
                dir[numdir++] = 2
            if(y<MHEIGHT-1&&(maze[x][y+1].and(16))==0)
                dir[numdir++] = 3
            var d:Int = (Math.random()*numdir).toInt()
            d = dir[d]

            // xor the wall in order to turn off the bit not minus because of 2's complements
            when (d) {
                0 -> {
                    maze[x][y] = (maze[x][y].xor(LEFT))
                    maze[x-1][y] = (maze[x-1][y].xor(RIGHT))
                }
                1 -> {
                    maze[x][y] = (maze[x][y].xor(RIGHT))
                    maze[x+1][y] = (maze[x+1][y].xor(LEFT))
                }
                2 -> {
                    maze[x][y] = (maze[x][y].xor(UP))
                    maze[x][y-1] = (maze[x][y-1].xor(DOWN))
                }
                3 -> {
                    maze[x][y] = (maze[x][y].xor(DOWN))
                    maze[x][y+1] = (maze[x][y+1].xor(UP))
                }
            }
            maze[x][y] -= 16

            var j: Int = 0

            //remove all of the visited coordinate
            while(j<blocks){
                if(maze[blockListX[j]][blockListY[j]].and(16)==0){
                    for(i in j until blocks-1){
                        blockListX[i]=blockListX[i+1]
                        blockListY[i]=blockListY[i+1]
                    }
                    blocks--
                    j=0
                }
                j++
            }
            //add remaining unvisited coordinate.
            if(x>0 && (maze[x-1][y].and(16))>0){
                blockListX[blocks] = x - 1
                blockListY[blocks] = y
                blocks++
            }
            if(x<MWIDTH-1 && (maze[x+1][y].and(16))>0){
                blockListX[blocks] = x + 1
                blockListY[blocks] = y
                blocks++
            }
            if(y>0&&(maze[x][y-1].and(16))>0){
                blockListX[blocks] = x
                blockListY[blocks] = y - 1
                blocks++
            }
            if(y<MHEIGHT-1 && (maze[x][y+1].and(16))>0){
                blockListX[blocks] = x
                blockListY[blocks] = y + 1
                blocks++
            }
        }
        //Make an exit at the bottom coordinate
        maze[MWIDTH-1][MHEIGHT-1]-=RIGHT
    }
    private fun doMazeGuide(moves:ArrayList<Int>){
        for (dir in moves){
            val x: Int = robotX
            val y: Int = robotY
            if((maze[x][y] and dir)==0){
                if(dir==LEFT) robotX--
                if(dir==RIGHT) robotX++
                if(dir==UP) robotY--
                if(dir==DOWN) robotY++
            }
            crumbs[x][y] = true
            mazecomp.repaint()
            try{
                Thread.sleep(SPEED.toLong())
            } catch (e: Exception){ }
        }
        println("DONE!!!!!")
    }
    private fun dfs(maze:Array<Array<Int>>):ArrayList<Int>{
        val orgState = State(robotX,robotY)
        val queueState: Deque<State> = ArrayDeque<State>()
        val moving:ArrayList<Int> = ArrayList()
        var currState:State?=null
        //keep track all of the move
        //var visited = mutableSetOf<Pair<Int, Int>>()
        queueState.add(orgState)
        while(!queueState.isEmpty()){
            currState = queueState.pop()
            //visited.add(Pair(currState.xCurr, currState.yCurr))
            if(currState.xCurr==MWIDTH-1 && currState.yCurr==MHEIGHT-1)
                break
            var childrenState:State? = null
            val allMoves:ArrayList<Int> = currState.allMoves(maze)
            for(move in allMoves){
                when(move){
                    1->{
                        childrenState = State(currState.xCurr,currState.yCurr - 1,1)
                    }
                    2->{
                        childrenState = State(currState.xCurr,currState.yCurr + 1,2)
                    }
                    4->{
                        childrenState = State(currState.xCurr - 1,currState.yCurr,4)
                    }
                    8->{
                        childrenState = State(currState.xCurr + 1,currState.yCurr,8)
                    }
                }
                var stop = false
                childrenState?.parent = currState
                var currPos = currState
                //
                while(currPos!=null){
                    if (currPos.xCurr == childrenState?.xCurr!! && currPos.yCurr == childrenState.yCurr){
                        stop = true
                        break
                    }
                    currPos = currPos.parent
                }
                if(!stop){
                    queueState.push(childrenState)
                }
            }
        }
        var prevState = currState
        while(prevState?.parent!=null){
            moving.add(prevState.recentMove)
            prevState = prevState.parent
        }
        return moving
    }
    private fun astar(maze: Array<Array<Int>>):ArrayList<Int>{
        val moving:ArrayList<Int> = ArrayList()
        var visited = mutableSetOf<Pair<Int, Int>>()
        var currState: State?= null
        var hq: PriorityQueue<Pair<Int, State>> = PriorityQueue<Pair<Int, State>>(compareBy { it.first })
        val orgState: State = State(robotX, robotY)
        hq.add(Pair(orgState.eval(), orgState))
        var i = 0
        while(!hq.isEmpty()){
            currState = hq.poll().second
            visited.add(Pair(currState.xCurr,currState.yCurr))
            if(currState.xCurr==MWIDTH-1 && currState.yCurr==MHEIGHT-1)
                break
            //all moves
            var childrenState:State? = null
            var allMoves = currState.allMoves(maze)

            for(move in allMoves){
                when(move){
                    1->{
                        childrenState = State(currState.xCurr,currState.yCurr - 1,1)
                    }
                    2->{
                        childrenState = State(currState.xCurr,currState.yCurr + 1,2)
                    }
                    4->{
                        childrenState = State(currState.xCurr - 1,currState.yCurr,4)
                    }
                    8->{
                        childrenState = State(currState.xCurr + 1,currState.yCurr,8)
                    }
                }
                childrenState?.parent = currState
                if(Pair(childrenState?.xCurr,childrenState?.yCurr) !in visited){
			        childrenState?.costsofar = currState.costsofar+1
			        hq.add(Pair(childrenState?.eval()!!,childrenState))
		        }
            }
            ++i
        }
        var prevState = currState
        while(prevState?.parent!=null){
            moving.add(prevState.recentMove)
            prevState = prevState.parent
        }
        return moving
    }
    inner class MazeComponent:JComponent(){
        override fun paintComponent(g: Graphics?) {
            super.paintComponent(g)
            g?.color = Color.WHITE
            g?.fillRect(0,0,MWIDTH*BLOCK,MHEIGHT*BLOCK)
            g?.color = Color(100,0,0)
            for(x in 0 until MWIDTH){
                for(y in 0 until MHEIGHT){
                    if(maze[x][y].and(1)>0)
                        g?.drawLine(x*BLOCK,y*BLOCK,x*BLOCK+BLOCK,y*BLOCK)
                    if (maze[x][y].and(2)>0)
                        g?.drawLine(x*BLOCK,y*BLOCK+BLOCK,x*BLOCK+BLOCK,y*BLOCK+BLOCK)
                    if(maze[x][y].and(4)>0)
                        g?.drawLine(x*BLOCK,y*BLOCK,x*BLOCK,y*BLOCK+BLOCK)
                    if(maze[x][y].and(8)>0)
                        g?.drawLine(x*BLOCK+BLOCK,y*BLOCK,x*BLOCK+BLOCK,y*BLOCK+BLOCK)
                }
            }
            if(robotActive){
                g?.color = Color.BLUE
                for(x in 0 until MWIDTH){
                    for (y in 0 until MHEIGHT){
                        if(crumbs[x][y])
                            g?.fillRect(x*BLOCK+BLOCK/2-1,y*BLOCK+BLOCK/2+1,2,2)
                    }
                }
                g?.color = Color.GREEN
                g?.fillOval(robotX*BLOCK+1, robotY*BLOCK+1,BLOCK-2,BLOCK-2)
            }
        }
    }
    inner class State (xcoordinate:Int, ycoordinate:Int, prevMove:Int = 0){
        var parent:State?= null
        var xCurr: Int = xcoordinate
        var yCurr: Int = ycoordinate
        var recentMove: Int = prevMove
        var costsofar:Int = 0

        private fun checkMoves(move:Int, maze:Array<Array<Int>>): Boolean{
            if(maze[xCurr][yCurr].and(move)==0)
                return true
            return false
        }

        private fun manhatanheuristic():Int {
            val dx: Int = kotlin.math.abs(xCurr - (MWIDTH-1))
            val dy: Int = kotlin.math.abs(yCurr - (MHEIGHT-1))
            return dx+dy
        }

        fun eval():Int {
            return costsofar + manhatanheuristic()
        }

        fun allMoves(maze:Array<Array<Int>>):ArrayList<Int>{
            val movesList: ArrayList<Int> = ArrayList()
            val moves = arrayOf(LEFT,DOWN,RIGHT,UP)
            for (move in moves){
                if(checkMoves(move,maze))
                    movesList.add(move)
            }
            return movesList
        }

    }
}