/*
  * This file is part of HyperCeiler.

  * HyperCeiler is free software: you can redistribute it and/or modify
  * it under the terms of the GNU Affero General Public License as
  * published by the Free Software Foundation, either version 3 of the
  * License.

  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU Affero General Public License for more details.

  * You should have received a copy of the GNU Affero General Public License
  * along with this program.  If not, see <https://www.gnu.org/licenses/>.

  * Copyright (C) 2023-2025 HyperCeiler Contributions
*/
package com.sevtinge.hyperceiler.hook.module.hook.systemui.lockscreen

import android.widget.ImageView
import com.sevtinge.hyperceiler.hook.module.base.BaseHook
import com.sevtinge.hyperceiler.hook.utils.devicesdk.isMoreHyperOSVersion
import com.sevtinge.hyperceiler.hook.utils.getObjectFieldOrNullAs
import de.robv.android.xposed.XposedHelpers
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder.`-Static`.methodFinder
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil.loadClassOrNull
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createHook

object HideLockScreenHint : BaseHook() {
    private val keyguardIndicationController by lazy {
        loadClassOrNull("com.android.systemui.statusbar.KeyguardIndicationController")
    }

    override fun init() {
        if (isMoreHyperOSVersion(2f)) {
            keyguardIndicationController!!.methodFinder()
                .filterByParamCount(1)
                .filterByParamTypes(keyguardIndicationController)
                .filterStatic().single().createHook {
                    returnConstant(null)
                }
        } else {
            // by Hyper Helper
            keyguardIndicationController!!.methodFinder()
                .filterByName("updateDeviceEntryIndication")
                .single().createHook {
                    after {
                        XposedHelpers.setObjectField(it.thisObject, "mPersistentUnlockMessage", "")
                    }
                }

            keyguardIndicationController!!.methodFinder()
                .filterByName("setIndicationArea")
                .single().createHook {
                    after {
                        val image =
                            it.thisObject.getObjectFieldOrNullAs<ImageView>("mUpArrow") ?: return@after
                        image.alpha = 0.0f
                    }
                }
        }
    }
}
