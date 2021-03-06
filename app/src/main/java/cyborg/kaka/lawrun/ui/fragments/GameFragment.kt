package cyborg.kaka.lawrun.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.topjohnwu.superuser.ShellUtils
import com.topjohnwu.superuser.io.SuFile
import cyborg.kaka.lawrun.R
import cyborg.kaka.lawrun.databinding.FragmentGameBinding
import cyborg.kaka.lawrun.utils.Constants
import cyborg.kaka.lawrun.utils.Constants.PROP_HDR_FILE_PATH
import cyborg.kaka.lawrun.utils.Constants.PUBGM_HDR_FILE
import cyborg.kaka.lawrun.utils.Constants.PUBGM_KR_HDR_FILE
import cyborg.kaka.lawrun.utils.Utils


class GameFragment : Fragment() {

    private lateinit var layout: FragmentGameBinding // ViewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        layout = FragmentGameBinding.inflate(inflater, container, false)

        // Game Tweaks Switch Listener
        layout.switchGameTweaks.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) Utils.setProp(Constants.PROP_GAME_TWEAKS, "1")
            else Utils.setProp(Constants.PROP_GAME_TWEAKS, "0")
            Utils.needRebootToast(activity)
            getGameTweaks()
        }

        // HDR Extreme Switch Listener
        layout.switchHdrExtreme.setOnCheckedChangeListener { _, isChecked ->
            if (Utils.rootCheck(activity)) {
                if (isChecked) {
                    if (SuFile.open("/storage/emulated/0/Android/data/com.tencent.ig/").exists()) {
                        ShellUtils.fastCmd("cp -a ${Utils.getProp(PROP_HDR_FILE_PATH)} $PUBGM_HDR_FILE") // PUBGM HDR Extreme On
                    }
                    if (SuFile.open("/storage/emulated/0/Android/data/com.pubg.krmobile/")
                            .exists()
                    ) {
                        ShellUtils.fastCmd("cp -a ${Utils.getProp(PROP_HDR_FILE_PATH)} $PUBGM_KR_HDR_FILE") // PUBGM Korean HDR Extreme On
                    }
                } else {
                    ShellUtils.fastCmd("rm $PUBGM_HDR_FILE") // PUBGM HDR Extreme Off
                    ShellUtils.fastCmd("rm $PUBGM_KR_HDR_FILE") // PUBGM Korean HDR Extreme Off
                }
            }
            getHDRExtreme() // Update HDR Extreme Switch State
        }

        // Show Layout
        return layout.root
    }

    // Reset Colors
    private fun resetColors() {
        // GetTheme Color
        val mainContainer: LinearLayout = activity!!.findViewById(R.id.mainContainer)
        val colorHolder = mainContainer.findViewById<TextView>(R.id.tab_color_holder)
        val themeColor =
            colorHolder.textColors.defaultColor // Applied Profile Color From Main Activity

        // Change Card Title Colors To Theme
        layout.tvGameTweaks.setTextColor(themeColor) // Game Tweaks Card Title Color
        layout.tvHdrExtreme.setTextColor(themeColor) // HDR Extreme Card Title Color
    }

    // Game Tweaks Switch State
    private fun getGameTweaks() {
        layout.switchGameTweaks.isChecked = Utils.getProp(Constants.PROP_GAME_TWEAKS) == "1"
    }

    // HDR Extreme Switch State
    private fun getHDRExtreme() {
        if ((!SuFile.open("/storage/emulated/0/Android/data/com.tencent.ig/").exists()
                    && !SuFile.open("/storage/emulated/0/Android/data/com.pubg.krmobile/").exists())
            || Utils.getProp(PROP_HDR_FILE_PATH) == ""
            || !SuFile.open(Utils.getProp(PROP_HDR_FILE_PATH)).exists()
        ) {
            layout.cardHdrExtreme.visibility = View.GONE // HDR Extreme Card Hide
            return
        }
        if (SuFile.open(PUBGM_HDR_FILE).exists() || SuFile.open(PUBGM_KR_HDR_FILE).exists()) {
            layout.switchHdrExtreme.isChecked = true // HDR Extreme Switch State On
            return
        }
        layout.switchHdrExtreme.isChecked = false // HDR Extreme Switch State Off
    }

    // Refresh Fragment
    override fun onStart() {
        super.onStart()
        resetColors() // Apply Current Profile Theme Color
        getGameTweaks() // Update Game Tweaks Switch State
        getHDRExtreme() // Update HDR Extreme Switch State
    }
}
