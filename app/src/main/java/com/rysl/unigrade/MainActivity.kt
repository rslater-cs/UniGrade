package com.rysl.unigrade

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.rysl.unigrade.constants.*
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
    }

    //Main UI methods

    fun setDatabase(){
        database = SQLAccess(SQLiteHelper(this))
    }

    fun initialiseUI(){
        clearUI()

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

        makeMenuWidgets()
    }

    fun clearUI(){
        buttons.clear()
        editTexts.clear()
        textViews.clear()
        bars.clear()
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

    //Summary UI Methods

    fun makeFinalCardWidgets(){
        clearUI()

        textViews.add(findViewById(R.id.title))
        textViews.add(findViewById(R.id.percent))
        textViews.add(findViewById(R.id.type))
        textViews.add(findViewById(R.id.resultBar))
        textViews.add(findViewById(R.id.description))
        textViews.add(findViewById(R.id.currentResult))

        buttons.add(findViewById(R.id.submitButton))
        buttons.add(findViewById(R.id.enterDescription))

        bars.add(findViewById(R.id.result))

        var typeContent = "Coursework"
        if(currentLearner!!.getType()!!)typeContent = "Test"
        textViews[TYPE].text = typeContent

        textViews[TITLE].text =  currentLearner!!.getName()
        textViews[PERCENT].text = "${currentLearner!!.getPercentage()}%"
        textViews[DESCRIPTION].text = getDescription()
        textViews[RESULT].text = getResult()

        bars[RESULTBAR].setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                textViews[BARVALUE].text = bars[RESULTBAR].getProgress().toString() + "%"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        val learner = currentLearner!!
        buttons[SUBMITBUTTON].setOnClickListener(View.OnClickListener {
            database.setResult(learner, bars[RESULTBAR].getProgress())
            textViews[RESULT].text = getResult()
        })

        buttons[DESCRIPTIONBUTTON].setOnClickListener(View.OnClickListener {
            tableIndex++
            setContentView(R.layout.description_former)
            makeDescriptionFormer()
        })

    }

    fun getDescription():String{
        val description = database.getDescription(currentLearner!!)

        if(description == null){
            textViews[DESCRIPTIONBUTTON].text = "add description"
            return "No description set"
        }
        return description
    }

    fun getResult():String{
        val result = database.getResult(currentLearner!!)

        if(result == -1){
            return "No result entered"
        }
        return "Current result: $result%"
    }

    fun makeDescriptionFormer(){
        val descriptionEnterButton = findViewById<Button>(R.id.submitDescription)
        val descriptionTextBBox = findViewById<EditText>(R.id.editDescription)

        descriptionEnterButton.setOnClickListener(View.OnClickListener {
            database.setDescription(currentLearner!!, descriptionTextBBox.text.toString())
            tableIndex--
            setContentView(R.layout.final_card_layout)
            makeFinalCardWidgets()
        })
    }


    //----------------------------------------------------------------------------------------------

    fun getLearner(): ArrayList<Learner>{
        val learners = database.getLearner(currentLearner, tables[tableIndex])
        learners.forEach{item ->
            item.setWorkingPercentage(database.getPredictedPercentage(item))
        }
        return learners
    }

    //----------------------------------------------------------------------------------------------

    override fun recyclerViewListClicked(position: Int) {
        if(position == 0)createLearnerMenu(tables[tableIndex])
        else{
            if(!menuVisible){
                pastLearners.push(currentLearner)
                currentLearner = currentLearners[position-1]
                tableIndex++
                updateCard()
                changeDataset()
                vibrator.vibrate(5)
            }
        }
    }

    override fun recyclerViewListSwiped(position: Int) {
        recycler.deleteLearner(position)
        deleteFromDatabase(currentLearners[position-1])
        currentLearners.removeAt(position-1)
        vibrator.vibrate(100)
    }

    override fun recyclerViewListLongClicked(position: Int) {
        if(!menuVisible && position != 0){
            editLearnerMenu(position)
            vibrator.vibrate(400)
        }
    }

    override fun menuVisible(): Boolean {
        return menuVisible
    }

    @Override
    override fun onBackPressed() {
        if(tableIndex == 0 && !menuVisible){
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        } else{
            if(menuVisible){
                createLearnerMenu(tables[tableIndex])
            } else{
                tableIndex--
                if(tableIndex < 3)currentLearner = pastLearners.pop()
                updateCard()
                changeDataset()
            }
            if(tableIndex == 2){
                setContentView(R.layout.activity_main)
                createUI()
            }
        }
    }
}