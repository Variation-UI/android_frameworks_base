/*
 * Copyright (C) 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settingslib.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceGroup
import androidx.preference.PreferenceViewHolder
import com.android.settingslib.widget.category.R

/** A [PreferenceCategory] that has no title. */
class UntitledPreferenceCategory
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : PreferenceCategory(context, attrs, defStyleAttr, defStyleRes) {
    init {
        layoutResource =
            when (SettingsThemeHelper.isExpressiveTheme(context)) {
                true -> R.layout.settingslib_expressive_untitled_preference_category
                else -> R.layout.settingslib_untitled_preference_category
            }
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        holder.isDividerAllowedAbove = false
        holder.isDividerAllowedBelow = false
        holder.itemView.updateDividerVisibility(shouldShowDivider())
        holder.findViewById(R.id.settingslib_untitled_preference_category_divider)?.visibility =
            holder.itemView.visibility
    }

    private fun View.updateDividerVisibility(showDivider: Boolean) {
        val originalLayoutParams =
            getTag(R.id.settingslib_untitled_preference_category_divider) as? LayoutParamsSnapshot
                ?: LayoutParamsSnapshot.from(layoutParams).also {
                    setTag(R.id.settingslib_untitled_preference_category_divider, it)
                }

        visibility = if (showDivider) View.VISIBLE else View.GONE
        layoutParams = layoutParams.apply {
            height = if (showDivider) originalLayoutParams.height else 0
            if (this is ViewGroup.MarginLayoutParams) {
                topMargin = if (showDivider) originalLayoutParams.topMargin else 0
                bottomMargin = if (showDivider) originalLayoutParams.bottomMargin else 0
            }
        }
    }

    private fun shouldShowDivider(): Boolean {
        if (!hasVisibleChildren(this)) {
            return false
        }

        val parentGroup = parent ?: return false
        for (i in 0 until parentGroup.preferenceCount) {
            val preference = parentGroup.getPreference(i)
            if (preference === this) {
                return false
            }
            if (preference is PreferenceCategory && hasVisibleChildren(preference)) {
                return true
            }
        }
        return false
    }

    private data class LayoutParamsSnapshot(
        val height: Int,
        val topMargin: Int,
        val bottomMargin: Int,
    ) {
        companion object {
            fun from(params: ViewGroup.LayoutParams): LayoutParamsSnapshot {
                return LayoutParamsSnapshot(
                    height = params.height,
                    topMargin = (params as? ViewGroup.MarginLayoutParams)?.topMargin ?: 0,
                    bottomMargin = (params as? ViewGroup.MarginLayoutParams)?.bottomMargin ?: 0,
                )
            }
        }
    }

    private fun hasVisibleChildren(group: PreferenceGroup): Boolean {
        for (i in 0 until group.preferenceCount) {
            if (group.getPreference(i).isVisible) {
                return true
            }
        }
        return false
    }
}
