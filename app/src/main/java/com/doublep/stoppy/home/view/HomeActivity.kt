package com.doublep.stoppy.home.view

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.doublep.stoppy.R
import com.doublep.stoppy.data.ButtonStyleItem
import com.doublep.stoppy.data.PrefsHelper
import com.doublep.stoppy.data.ThemePack
import com.doublep.stoppy.databinding.AcHomePageBinding
import com.doublep.stoppy.home.others.ButtonStyleAdapter
import com.doublep.stoppy.home.others.LapAdapter
import com.doublep.stoppy.home.viewmodel.HomeViewModel


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: AcHomePageBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var lapAdapter: LapAdapter
    private lateinit var progressAnimator: ValueAnimator
    private var currentProgress = 0


    private val fontNames = arrayOf("Digital", "LCD", "Sporty", "Simple")
    private val fontIds = arrayOf(
        R.font.bitcountgridsingleregular,
        R.font.dsdiggi,
        R.font.orbitronregular,
        R.font.robotovariable
    )

    private val themes by lazy {
        listOf(
            ThemePack(
                "Classic",
                getColorCompat(R.color.theme_classic_bg),
                getColorCompat(R.color.theme_classic_font),
                2
            ),
            ThemePack(
                "Retro Pink",
                getColorCompat(R.color.theme_retro_pink_bg),
                getColorCompat(R.color.theme_retro_pink_font),
                0
            ),
            ThemePack(
                "Cool Neon",
                getColorCompat(R.color.theme_cool_neon_bg),
                getColorCompat(R.color.theme_cool_neon_font),
                1
            ),
            ThemePack(
                "Soft Sky",
                getColorCompat(R.color.theme_soft_sky_bg),
                getColorCompat(R.color.theme_soft_sky_font),
                3
            ),
            ThemePack(
                "Mint Green",
                getColorCompat(R.color.theme_mint_green_bg),
                getColorCompat(R.color.theme_mint_green_font),
                1
            ),
            ThemePack(
                "Sunset Orange",
                getColorCompat(R.color.theme_sunset_orange_bg),
                getColorCompat(R.color.theme_sunset_orange_font),
                2
            ),
            ThemePack(
                "Deep Purple",
                getColorCompat(R.color.theme_deep_purple_bg),
                getColorCompat(R.color.theme_deep_purple_font),
                3
            )
        )
    }

    private val buttonStyleItems = listOf(
        ButtonStyleItem("Default", R.drawable.btn_style_default, R.drawable.btn_style_default),
        ButtonStyleItem("Outline", R.drawable.btn_style_outline, R.drawable.btn_style_outline),
        ButtonStyleItem("Flat", R.drawable.btn_style_flat, R.drawable.btn_style_flat),
        ButtonStyleItem("Neon", R.drawable.btn_style_neon, R.drawable.btn_style_neon),
        ButtonStyleItem("Ghost", R.drawable.btn_style_ghost, R.drawable.btn_style_ghost),
        ButtonStyleItem("Glass", R.drawable.btn_style_glass, R.drawable.btn_style_glass),
        ButtonStyleItem("Frost", R.drawable.btn_style_frost, R.drawable.btn_style_frost)
    )

    val buttonAdapter = ButtonStyleAdapter(this, buttonStyleItems)

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.ac_home_page)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        binding.homeViewmodel = viewModel
        binding.lifecycleOwner = this

        lapAdapter = LapAdapter(viewModel.lapList, this)
        binding.recyLaps.adapter = lapAdapter
        binding.recyLaps.layoutManager = LinearLayoutManager(this)

        val (savedTime, savedLaps) = PrefsHelper.loadState(this)
        viewModel.lapList.addAll(savedLaps)
        viewModel.setTimerTime(savedTime)
        lapAdapter.notifyDataSetChanged()

        if (savedLaps.isNotEmpty()){
            binding.lblLapsSection.visibility = View.GONE
            binding.lblTimeLaps.visibility = View.VISIBLE
        }


        val (digitColor, bgColor) = PrefsHelper.loadColors(this)
        val fontIndex = PrefsHelper.loadFontName(this) ?: 0
        val btnStyleIndex = PrefsHelper.loadButtonStyleIndex(this)

        binding.tvTimer.setTextColor(digitColor)
        binding.tvTimer.backgroundTintList = ColorStateList.valueOf(bgColor)
        binding.tvTimer.typeface = ResourcesCompat.getFont(this, fontIds[fontIndex])

        applyButtonStyle(btnStyleIndex)

        binding.btnStartStop.setOnClickListener {
            changeStartStopBtnUIAndPerformClick()
        }

        binding.btnLapReset.setOnClickListener {
            changeLapResetBtnUIAndPerformClick()
        }


        binding.fabMain.setOnClickListener {
            binding.fabMenu.visibility =
                if (binding.fabMenu.isVisible) View.GONE else View.VISIBLE
        }

        binding.fabTheme.setOnClickListener {
            showThemeChooser()
        }

        binding.fabFont.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Choose Font")
                .setItems(fontNames) { _, fontIndex ->
                    val selectedTypeface = ResourcesCompat.getFont(this, fontIds[fontIndex])
                    binding.tvTimer.typeface = selectedTypeface
                    PrefsHelper.saveFontName(this, fontIndex)
                }
                .show()
        }

        binding.fabFontColor.setOnClickListener {
            openColorPicker { color ->
                binding.tvTimer.setTextColor(color)
                PrefsHelper.saveColors(
                    this,
                    color,
                    binding.tvTimer.backgroundTintList?.defaultColor ?: Color.TRANSPARENT
                )
            }
        }

        binding.fabBackground.setOnClickListener {
            openColorPicker { color ->
                binding.tvTimer.backgroundTintList = ColorStateList.valueOf(color)
                PrefsHelper.saveColors(this, binding.tvTimer.currentTextColor, color)
            }
        }

        binding.fabButtonStyle.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Choose Button Style")
                .setAdapter(buttonAdapter) { _, index ->
                    applyButtonStyle(index)
                    PrefsHelper.saveButtonStyleIndex(this, index)
                }
                .show()
        }
    }

    private fun showThemeChooser() {
        val themeNames = themes.map { it.name }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Select Theme")
            .setItems(themeNames) { _, which ->
                applyTheme(themes[which])
            }
            .show()
    }


    private fun applyTheme(theme: ThemePack) {
        binding.tvTimer.setTextColor(theme.fontColor)
        binding.tvTimer.typeface = ResourcesCompat.getFont(this, fontIds[theme.fontIndex])
        binding.tvTimer.backgroundTintList = ColorStateList.valueOf(theme.backgroundColor)

        PrefsHelper.saveColors(this, theme.fontColor, theme.backgroundColor)
        PrefsHelper.saveFontName(this, theme.fontIndex)
    }

    private fun applyButtonStyle(index: Int) {
        val selectedStyle = buttonStyleItems[index]
        val drawable = ContextCompat.getDrawable(this, selectedStyle.styleDrawableId)
        binding.btnStartStop.background = drawable
        binding.btnLapReset.background = drawable
    }

    private fun changeStartStopBtnUIAndPerformClick() {
        if (binding.btnStartStop.text == resources.getString(R.string.btn_start)) {
            binding.btnStartStop.text = resources.getString(R.string.btn_stop)
            binding.btnLapReset.text = resources.getString(R.string.btn_lap)

            binding.btnStartStop.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_stop,
                0,
                0,
                0
            )
            binding.btnLapReset.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lap, 0, 0, 0)
            startProgressRing()
            viewModel.start()
        } else {
            binding.btnStartStop.text = resources.getString(R.string.btn_start)
            binding.btnLapReset.text = resources.getString(R.string.btn_reset)

            binding.btnStartStop.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_play,
                0,
                0,
                0
            )
            binding.btnLapReset.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_reset,
                0,
                0,
                0
            )
            stopProgressRing()
            viewModel.stop()
        }
        triggerHapticFeedback()
    }

    private fun changeLapResetBtnUIAndPerformClick() {
        if (binding.btnLapReset.text == resources.getString(R.string.btn_lap)) {
            binding.lblLapsSection.visibility = View.GONE
            binding.lblTimeLaps.visibility = View.VISIBLE
            viewModel.lap()
            lapAdapter.notifyItemInserted(0)
            binding.recyLaps.scrollToPosition(0)
        } else {
            binding.lblLapsSection.visibility = View.VISIBLE
            binding.lblTimeLaps.visibility = View.GONE
            binding.btnStartStop.text = resources.getString(R.string.btn_start)
            binding.btnStartStop.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_play,
                0,
                0,
                0
            )
            resetProgressRing()
            viewModel.reset()
            lapAdapter.notifyDataSetChanged()
        }
        triggerHapticFeedback()
    }

    override fun onPause() {
        super.onPause()
        PrefsHelper.saveState(this, viewModel.getTime(), viewModel.lapList)
    }

    private fun getColorCompat(colorResId: Int): Int {
        return ContextCompat.getColor(this, colorResId)
    }


    private fun triggerHapticFeedback() {
        binding.root.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }

    private fun startProgressRing() {
        stopProgressRing()
        animateStart()
        progressAnimator = ValueAnimator.ofInt(currentProgress, 360).apply {
            duration = 60_000L
            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE

            addUpdateListener {
                val value = it.animatedValue as Int
                currentProgress = value
                binding.progressRing.progress = value
            }
        }
        progressAnimator.start()

    }

    private fun animateStart() {
        binding.tvTimer.animate()
            .scaleX(1.1f).scaleY(1.1f)
            .setDuration(150)
            .withEndAction {
                binding.tvTimer.animate()
                    .scaleX(1f).scaleY(1f)
                    .setDuration(100)
                    .start()
            }.start()

        binding.btnStartStop.animate()
            .alpha(0f)
            .setDuration(100)
            .withEndAction {
                binding.btnStartStop.alpha = 0f
                binding.btnStartStop.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .start()
            }.start()
    }

    private fun animateStop() {
        binding.tvTimer.animate()
            .alpha(0.7f)
            .setDuration(150)
            .withEndAction {
                binding.tvTimer.animate()
                    .alpha(1f)
                    .setDuration(150)
                    .start()
            }.start()

        binding.btnStartStop.animate()
            .rotationBy(10f)
            .setDuration(50)
            .withEndAction {
                binding.btnStartStop.animate()
                    .rotationBy(-20f)
                    .setDuration(100)
                    .withEndAction {
                        binding.btnStartStop.animate().rotation(0f).setDuration(50).start()
                    }.start()
            }.start()
    }

    private fun animateReset() {
        binding.tvTimer.animate()
            .rotationBy(360f)
            .setDuration(500)
            .start()

        binding.btnLapReset.animate()
            .alpha(0.5f)
            .setDuration(100)
            .withEndAction {
                binding.btnLapReset.animate()
                    .alpha(1f)
                    .setDuration(200)
                    .start()
            }.start()
    }

    private fun stopProgressRing() {
        animateStop()
        if (::progressAnimator.isInitialized) {
            progressAnimator.cancel()
        }
    }

    private fun resetProgressRing() {
        stopProgressRing()
        animateReset()
        currentProgress = 0
        binding.progressRing.progress = 0
    }

    private fun openColorPicker(onColorPicked: (Int) -> Unit) {
        val colors = arrayOf(
            "White",
            "Black",
            "Cool Gray",
            "Charcoal",
            "Soft Beige",
            "Warm Sand",
            "Peach Pink",
            "Rose Gold",
            "Baby Blue",
            "Sky Blue",
            "Soft Purple",
            "Indigo",
            "Mint Green",
            "Emerald",
            "Lemon Yellow",
            "Burnt Orange",
            "Crimson Red",
            "Deep Teal",
            "Neon Cyan"
        )

        val colorValues = arrayOf(
            R.color.user_white,
            R.color.user_black,
            R.color.user_cool_gray,
            R.color.user_charcoal,
            R.color.user_soft_beige,
            R.color.user_warm_sand,
            R.color.user_peach_pink,
            R.color.user_rose_gold,
            R.color.user_baby_blue,
            R.color.user_sky_blue,
            R.color.user_soft_purple,
            R.color.user_indigo,
            R.color.user_mint_green,
            R.color.user_emerald,
            R.color.user_lemon_yellow,
            R.color.user_burnt_orange,
            R.color.user_crimson_red,
            R.color.user_deep_teal,
            R.color.user_neon_cyan
        )

        AlertDialog.Builder(this)
            .setTitle("Choose Color")
            .setItems(colors) { _, which ->
                onColorPicked(ContextCompat.getColor(this, colorValues[which]))
            }
            .show()
    }
}



