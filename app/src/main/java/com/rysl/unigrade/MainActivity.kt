package com.rysl.unigrade

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
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

    val currentLearners = ArrayList<Learner>()
    val pastLearners = Stack<Learner>()

    var tables = arrayOf("subject", "module", "assignment", "end", "card")
    var tableIndex = 0
    var menuVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
                menuPercent.setText(bars[PERCENTBAR].progress.toString() + "%")
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