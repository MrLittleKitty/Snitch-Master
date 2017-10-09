package com.gmail.nuclearcat1337.snitch_master.gui.controls;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class TextBox extends GuiTextField {
	private final String numericRegex;
	private boolean number, allowNegative;
	private Integer clampedMinimum;
	private Integer clampedMaximum;

	public TextBox(String text, FontRenderer renderer, int x, int y, int width, int height, boolean numeric, boolean allowNegative, int maxStringLength) {
		super(0, renderer, x, y, width, height);
		super.setMaxStringLength(maxStringLength);
		super.setText(text);
		this.number = numeric;
		this.allowNegative = allowNegative;

		String regex = null;
		if (this.number) {
			if (this.allowNegative) {
				regex = "[^-?\\d]";
			} else {
				regex = "[^\\d]";
			}
		}

		this.numericRegex = regex;
	}

	public void setClamp(Integer clampedMinimum, Integer clampedMaximum) {
		this.clampedMinimum = clampedMinimum;
		this.clampedMaximum = clampedMaximum;
	}

	@Override
	public void writeText(String text) {
		super.writeText(text);
		if (this.number) {
			String fixed = getText().replaceAll(this.numericRegex, "");
			if (this.allowNegative) {
				String start = fixed.startsWith("-") ? "-" : "";
				fixed = start + fixed.replaceAll("-", "");
			}
			super.setText(fixed);
		}
	}

	@Override
	public boolean textboxKeyTyped(char par1, int par2) {
		boolean res = super.textboxKeyTyped(par1, par2);
		if ((this.number) && (isFocused())) {
			clamp();
		}
		return res;
	}

	public Integer clamp() {
		if (!this.number || this.clampedMinimum == null || this.clampedMaximum == null) {
			return null;
		}

		String text = getText();
		if (this.clampedMinimum != null) {
			if ((text == null) || (text.length() == 0) || (text.equals("-"))) {
				return null;
			}

			try {
				setText(Integer.valueOf(Math.max(this.clampedMinimum.intValue(), Integer.parseInt(text))).toString());
			} catch (Exception e) {
				setText(this.clampedMinimum.toString());
			}
			if (this.clampedMaximum != null) {
				try {
					setText(Integer.valueOf(Math.min(this.clampedMaximum.intValue(), Integer.parseInt(text))).toString());
				} catch (Exception e) {
					setText(this.clampedMaximum.toString());
				}
			}
		}
		try {
			return Integer.valueOf(Integer.parseInt(text));
		} catch (Exception e) {}
		return null;
	}
}
