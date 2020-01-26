package com.rysl.unigrade

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.rysl.unigrade.constants.MENUPERCENT
import com.rysl.unigrade.constants.PERCENTBAR
import com.rysl.unigrade.constants.TABLENAME
import com.rysl.unigrade.database.SQLAccess
import com.rysl.unigrade.database.SQLiteHelper
import com.rysl.unigrade.learningTree.Learner
import com.rysl.unigrade.recyclerInterface.RecyclerViewClickListener
import com.rysl.unigrade.recyclerInterface.RecyclerViewInterface
import java.util.*
import kotlin.collections.ArrayList

class MainActivity: AppCompatActivity(), RecyclerViewClickListener {
    lateinit var database: SQLAccess
    lateinit var recycler: RecyclerViewInterface
    lateinit var learnerAddMenu: CardView
    var currentLearner: Learner? = null
    lateinit var vibrator: Vibrator
    lateinit var spinnerType: Spinner

    val buttons = ArrayList<Button>()
    val textViews = ArrayList<TextView>()
    val editTexts = ArrayList<EditText>()
    val bars = ArrayList<SeekBar>();

    var currentLearners = ArrayList<Learner>()
    val pastLearners = Stack<Learner>()

    var tables = arrayOf("subject", "module", "assignment", "end", "card")
    var tableIndex = 0
    var menuVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setDatabase()
        initialiseUI()
        makeMenuWidgets()
    }

    fun setDatabase(){
        database = SQLAccess(SQLiteHelper(this))
    }

    fun initialiseUI(){
        val view = findViewById<RecyclerView>(R.id.recycler)
        bars.add(findViewById(R.id.percentBar))
        currentLearners = getLearner()

        bars[PERCENTBAR].setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                editTexts[MENUPERCENT].setText(bars[PERCENTBAR].progress.toString() + "%")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        recycler = RecyclerViewInterface(this, view, currentLearners, this)

        textViews.add(findViewById(R.id.tableName))

        textViews[TABLENAME].text = tables[tableIndex]

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun makeMenuWidgets(){
        learnerAddMenu = findViewById(R.id.addMenu)

        buttons.add(findViewById(R.id.testButton))

        editTexts.add(findViewById(R.id.testInput))
        editTexts.add(findViewById(R.id.testInput2))

        textViews.add(findViewById(R.id.menuTitle))
        textViews.add(findViewById(R.id.percentTitle))
        textViews.add(findViewById(R.id.typeTitle))

        spinnerType = findViewById(R.id.spinner)
        val types = arrayOf("Test", "Coursework")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = adapter
    }

    fun makeFinalCardWidgets(){

    }

    //----------------------------------------------------------------------------------------------

    fun getLearner(): ArrayList<Learner>{
        val learners = database.getLearner(currentLearner, tables[tableIndex])
        learners.forEach(){item ->
            item.setWorkingPercentage(database.getPredictedPercentage(item))
        }
        return learners
    }

    //----------------------------------------------------------------------------------------------

    override fun recyclerViewListClicked(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun recyclerViewListSwiped(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun recyclerViewListLongClicked(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun menuVisible(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}