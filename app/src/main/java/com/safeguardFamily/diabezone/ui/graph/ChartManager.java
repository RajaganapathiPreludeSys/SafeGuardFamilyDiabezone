package com.safeguardFamily.diabezone.ui.graph;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.safeguardFamily.diabezone.ui.graph.animation.AnimationManager;
import com.safeguardFamily.diabezone.ui.graph.animation.data.AnimationValue;
import com.safeguardFamily.diabezone.ui.graph.draw.DrawManager;
import com.safeguardFamily.diabezone.ui.graph.draw.data.Chart;

public class ChartManager implements AnimationManager.AnimationListener {

	private final DrawManager drawManager;
	private final AnimationManager animationManager;
	private final AnimationListener listener;

	public interface AnimationListener {

		void onAnimationUpdated();
	}


	public ChartManager(@NonNull Context context, @Nullable AnimationListener listener) {
		this.drawManager = new DrawManager(context);
		this.animationManager = new AnimationManager(drawManager.chart(), this);
		this.listener = listener;
	}

	public Chart chart() {
		return drawManager.chart();
	}

	public DrawManager drawer() {
		return drawManager;
	}

	public void animate() {
		if (!drawManager.chart().getDrawData().isEmpty()) {
			animationManager.animate();
		}
	}

	@Override
	public void onAnimationUpdated(@NonNull AnimationValue value) {
		drawManager.updateValue(value);
		if (listener != null) {
			listener.onAnimationUpdated();
		}
	}
}
