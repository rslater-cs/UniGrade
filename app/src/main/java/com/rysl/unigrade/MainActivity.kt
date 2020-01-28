package com.rysl.unigrade

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rysl.unigrade.constants.*
import com.rysl.unigrade.database.SQLAccess
import com.rysl.unigrade.database.SQLiteHelper
import com.rysl.unigrade.learningTree.Learner
import com.rysl.unigrade.recyclerInterface.RecyclerViewClickListener
import com.rysl.unigrade.recyclerInterface.RecyclerViewInterface
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity: AppCompatActivity(), RecyclerViewClickListener {
    private lateinit var database: SQLAccess
    private lateinit var recycler: RecyclerViewInterface
    private lateinit var learnerAddMenu: CardView
    private var currentLearner: Learner? = null
    private lateinit var vibrator: Vibrator
    private lateinit var spinnerType: Spinner
    private lateinit var darkLightButton: FloatingActionButton

    private val buttons = ArrayList<Button>()
    private val textViews = ArrayList<TextView>()
    private val editTexts = ArrayList<EditText>()
    private val bars = ArrayList<SeekBar>();

    private var currentLearners = ArrayList<Learner>()
    private val pastLearners = Stack<Learner>()

    private var tables = arrayOf("subject", "module", "assignment", "end", "card")
    private var tableIndex = 0
    private var menuVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        /*if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            setTheme(R.style.Theme_AppCompat_NoActionBar)
            println("YES")
        } else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            setTheme(R.style.Theme_AppCompat_Light_NoActionBar)
            println("NO")
        }*/
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

        darkLightButton = findViewById(R.id.LightDark)
        /*
        darkLightButton.setOnClickListener {
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                setTheme(R.style.Theme_AppCompat_NoActionBar)
                findViewById<ConstraintLayout>(R.id.background).rootView.setBackgroundColor(0)
                println("YES")
            } else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                setTheme(R.style.Theme_AppCompat_Light_NoActionBar)
                findViewById<ConstraintLayout>(R.id.background).rootView.setBackgroundColor(255)
                println("NO")
            }
            setContentView(R.layout.activity_main)
            setDatabase()
            initialiseUI()
        }*/

        val view = findViewById<RecyclerView>(R.id.recycler)
        bars.add(findViewById(R.id.percentBar))
        currentLearners = getLearner()

        bars[PERCENTBAR].setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                textViews[MENUPERCENT].setText(bars[PERCENTBAR].progress.toString() + "%")
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

        textViews.add(findViewById(R.id.menuTitle))
        textViews.add(findViewById(R.id.percentTitle))
        textViews.add(findViewById(R.id.typeTitle))
        textViews.add(findViewById(R.id.testInput2))

        spinnerType = findViewById(R.id.spinner)
        val types = arrayOf("Test", "Coursework")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = adapter
    }

    private fun enterButton(boolType: Boolean, isAdd: Boolean, learner: Learner?, position: Int){
        buttons[MENUBUTTON].setOnClickListener { enterMenu(boolType, isAdd, learner, position) }
    }

    fun enterMenu(type: Boolean, isAdd: Boolean, learner: Learner?, position: Int){
        val name = editTexts[MENUNAME].text.toString()
        var percent = -1
        var boolType: Boolean? = null

        if(type){
            percent = bars[PERCENTBAR].progress
            boolType = getType()
        }
        hideKeyboard(this)

        if(isAdd){
            database.setLearner(currentLearner, name, percent, boolType)
            updateButtonsFromLearners()
        } else{
            currentLearners[position-1] = learner!!
            database.editLearner(learner, name, percent, boolType)
            updateDataset(position)
        }
        addMenu.visibility = View.GONE
        menuVisible = !menuVisible
        vibrator.vibrate(100)
        vibrator.vibrate(100)
    }

    fun getType(): Boolean{
        if(spinnerType.selectedItem.toString() == "Test") return true
        return false
    }

    fun updateCard(){
        textViews[MENUTITLE].text = tables[tableIndex]
        textViews[TABLENAME].text = tables[tableIndex]
    }

    //Summary UI Methods

    private fun makeFinalCardWidgets(){
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
        buttons[SUBMITBUTTON].setOnClickListener {
            database.setResult(learner, bars[RESULTBAR].progress)
            textViews[RESULT].text = getResult()
        }

        buttons[DESCRIPTIONBUTTON].setOnClickListener {
            tableIndex++
            setContentView(R.layout.description_former)
            makeDescriptionFormer()
        }

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

    private fun makeDescriptionFormer(){
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
    //interacting with learners

    fun createLearnerMenu(table: String){
        textViews[MENUNAME].text = ""

        if(tableIndex < 3){
            var boolType = false
            if(menuVisible)addMenu.visibility = View.GONE
            else{
                textViews[MENUTITLE].text = table

                if(tableIndex == 2){
                    bars[PERCENTBAR].max = getMaxBarValue()
                    boolType = true
                }

                enterButton(boolType, true, null, -1)
                addMenu.visibility = View.VISIBLE
            }

            setPercentVisible(boolType)
            setTypeVisible(boolType)
            menuVisible = !menuVisible
        }
    }

    fun getLearner(): ArrayList<Learner>{
        val learners = database.getLearner(currentLearner, tables[tableIndex])
        learners.forEach{item ->
            item.setWorkingPercentage(database.getPredictedPercentage(item))
        }
        return learners
    }

    fun getMaxBarValue(): Int{
        var maxValue = 0
        currentLearners.forEach{ item ->
            maxValue += item.getPercentage()
        }
        return 100-maxValue
    }

    fun getViewState(visiblility: Boolean): Int{
        var viewState = View.GONE
        if(visiblility){
            viewState = View.VISIBLE
        }
        return viewState
    }

    fun setPercentVisible(visible: Boolean){
        val viewState = getViewState(visible)
        textViews[PERCENTTITLE].visibility = viewState
        textViews[MENUPERCENT].visibility = viewState
        bars[PERCENTBAR].visibility = viewState
    }

    fun setTypeVisible(visible: Boolean){
        val viewState = getViewState(visible)
        textViews[TYPETITLE].visibility = viewState
        spinnerType.visibility = viewState
    }

    fun updateButtonsFromLearners(){
        currentLearners = getLearner()
        recycler.addLearner(currentLearners)
    }

    fun updateDataset(position: Int){
        val updatedLearner = getLearner()[position-1]
        currentLearners[position-1] = updatedLearner
        recycler.updateLearner(updatedLearner, position)
    }

    fun changeDataset(){
        if(tableIndex == 3){
            setContentView(R.layout.final_card_layout)
            makeFinalCardWidgets()
        } else{
            currentLearners = getLearner()
            recycler.refreshView(currentLearners)
        }
    }

    fun deleteFromDatabase(learner: Learner){
        database.deleteLearner(learner.getKey(), learner.getTable())
    }

    fun editLearnerMenu(position: Int){
        val learnerToEdit = currentLearners[position-1]
        textViews[MENUTITLE].text = learnerToEdit.getName()
        editTexts[MENUNAME].setText("")
        var boolType = false

        if(tableIndex == 2){
            bars[PERCENTBAR].max = getMaxBarValue()+learnerToEdit.getPercentage()
            boolType = true
        }
        setPercentVisible(boolType)
        setTypeVisible(boolType)
        addMenu.visibility = View.VISIBLE
        enterButton(boolType, false, learnerToEdit, position)
        menuVisible = !menuVisible
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

    //System methods

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
                initialiseUI()
            }
        }
    }

    //CREDIT: https://stackoverflow.com/users/680583/rmirabelle
    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}