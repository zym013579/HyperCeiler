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
package com.sevtinge.hyperceiler.hook.module.hook.systemframework.display

import android.content.pm.ApplicationInfo
import com.sevtinge.hyperceiler.hook.module.base.BaseHook
import com.sevtinge.hyperceiler.hook.utils.api.LazyClass.clazzMiuiBuild
import com.sevtinge.hyperceiler.hook.utils.devicesdk.isInternational
import de.robv.android.xposed.XposedHelpers
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder.`-Static`.methodFinder
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil.loadClass
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil.setStaticObject
import io.github.kyuubiran.ezxhelper.core.util.ObjectUtil.invokeMethodBestMatch
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createHooks

// from SetoHook by SetoSkins
class AllDarkMode : BaseHook() {
    override fun init() {
        if (isInternational()) return
        val clazzForceDarkAppListManager =
            loadClass("com.android.server.ForceDarkAppListManager")
        clazzForceDarkAppListManager.methodFinder().filterByName("getDarkModeAppList").toList()
            .createHooks {
                before {
                    val originalValue = XposedHelpers.getStaticBooleanField(clazzMiuiBuild, "IS_INTERNATIONAL_BUILD")
                    setStaticObject(clazzMiuiBuild, "IS_INTERNATIONAL_BUILD", true)
                    it.setObjectExtra("originalValue", originalValue)
                }
                after {
                    val originalValue = it.getObjectExtra("originalValue")
                    setStaticObject(clazzMiuiBuild, "IS_INTERNATIONAL_BUILD", originalValue)
                }
            }
        clazzForceDarkAppListManager.methodFinder().filterByName("shouldShowInSettings").toList()
            .createHooks {
                before { param ->
                    val info = param.args[0] as ApplicationInfo?
                    param.result =
                        !(info == null || (
                            invokeMethodBestMatch(info, "isSystemApp") as Boolean
                            ) || info.uid < 10000)
                }
            }
    }
}
