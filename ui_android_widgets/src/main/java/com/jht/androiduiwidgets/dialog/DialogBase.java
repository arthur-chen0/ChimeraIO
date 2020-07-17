package com.jht.androiduiwidgets.dialog;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.jht.androidcommonalgorithms.util.ContextUtil;
import com.jht.androiduiwidgets.R;

/**
 * This class provides basic functionality for dialogs used throughout the project.  They key
 * functionality is that this will explode and implode the dialog from a view passed (anchor).
 * The user can also offset the dialog using the init() function. It assumes the dialog is modal and
 * touching outside can optionally close the dialog.  It catches all touches so you cannot touch
 * a view underneath the dialog.
 *
 *
 */
public abstract class DialogBase extends RelativeLayout {

    // This is the dialog inner container.
    private ViewGroup container;

    // The view to base the animations on.
    private View anchor;

    // True if we should close the dialog when they touch outside the container.
    private boolean closeOnOutsideTouch = false;

    // These are used to offse the dialog.  By default the dialog appears in the middle of the screen.
    private float paddingTop = 0;
    private float paddingStart = 0;


    /**
     * Initialize the dialog box.
     *
     * @param context   Current context for the view.
     */
    public DialogBase(Context context) {
        this(context, null, 0);
    }

    /**
     * Initialize the dialog box.
     *
     * @param context   Current context for the view.
     * @param attrs     Attributes that are set from the view's XML.
     */
    public DialogBase(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Initialize the dialog box.
     *
     * @param context   Current context for the view.
     * @param attrs     Attributes that are set from the view's XML.
     * @param defStyleAttr  Default style for the view.
     */
    @SuppressLint("ClickableViewAccessibility")
    public DialogBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        // Inflate the selector layout.
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        View v = LayoutInflater.from(context).inflate(getLayoutResId(), null, false);

        addView(v, lp);
        v.setOnTouchListener((v1, event) -> {
            Log.e("rootIsTouched","root");
            if(closeOnOutsideTouch) {
                hide();
            }
            return true;
        });

        // Cache the container.
        container = findViewById(R.id.dialog_container);

    }

    /**
     * Each subclass must define the layout resource id to load the base view.  the constructors
     * will call this.
     *
     * @return  The layout resource id for the dialog.
     */
    abstract protected int getLayoutResId();

    /**
     * Sets the close on outside touch state.
     *
     * @param closeOnOutsideTouch   If true close the dialog when the user touches outside the container.  If false swallow touches ouside the container.
     */
    public void setCloseOnOutsideTouch(boolean closeOnOutsideTouch) {
        this.closeOnOutsideTouch = closeOnOutsideTouch;
    }


    /**
     * Shows the dialog box exploding from the view (anchor) provided).
     *
     * @param anchor    The view that caused the dialog box to appear.
     */
    public void show(final View anchor) {
        // Store the anchor for use when we close the dialog.
        this.anchor = anchor;

        // Initialize the dialog box preparing for the show animation.
        container.setAlpha(0);
        this.setAlpha(1);

        // Add the dialog to the root view.
        Activity activity = ContextUtil.getActivity(anchor.getContext());
        View rootView;
        if(activity != null) {
            rootView = activity.findViewById(android.R.id.content);
        }
        else {
            rootView = anchor;
            while (rootView.getParent() != null && rootView.getParent() instanceof View && rootView.getParent() instanceof ViewGroup) {
                rootView = (View) rootView.getParent();
            }
        }
        if(rootView instanceof ViewGroup) {
            ((ViewGroup)rootView).addView(this);
        }



        // We must post the animation because Android will not measure the container until after
        // this call.  We just added it to the layout so we must do the show later.
        anchor.post(() -> {
            // Get the xy coordinates to explode from.
            int[] translationXY = getTranslationXY(anchor);

            // Move the container to the middle of the anchor.
            container.setTranslationX(translationXY[0]);
            container.setTranslationY(translationXY[1]);
            container.setScaleX(0);
            container.setScaleY(0);

            // Explode the popup moving it to the padding offset and scaling it from 0 to 1.
            container.animate().setDuration(400).alpha(1).translationY(paddingTop).translationX(paddingStart).scaleX(1).scaleY(1).setListener(new Animator.AnimatorListener() {
                /**
                 * Make sure the container is correctly aligned at the end of the animation.
                 */
                private void onAnimationComplete() {
                    container.setAlpha(1);
                    container.setScaleX(1);
                    container.setScaleY(1);
                    container.setTranslationX(paddingStart);
                    container.setTranslationY(paddingTop);

                }

                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    onAnimationComplete();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    onAnimationComplete();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            }).start();
        });
    }

    /**
     * This computes the point from which to explode from.
     * @param anchor    The view to explode from.  We will explode from the middle of the view.
     * @return          The computed translation XY
     */
    private int[] getTranslationXY(View anchor) {

        int[] anchorXY = new int[2];
        int[] containerXY = new int[2];
        int[] translationXY = new int[2];

        // Get the coordinates for the anchor and the container.
        container.getLocationOnScreen(containerXY);
        anchor.getLocationOnScreen(anchorXY);

        // Compute the translation point.
        translationXY[0] = (anchorXY[0] + anchor.getWidth() / 2) - (containerXY[0] + container.getWidth() / 2);
        translationXY[1] = (anchorXY[1] + anchor.getHeight() / 2) - (containerXY[1] + container.getHeight() / 2);

        return translationXY;
    }

    /**
     * Hide the dialog box imploding back into the anchor.
     */
    public void hide() {

        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        Context context = getContext();
        View view = null;
        if(context instanceof Activity) {
            Activity activity = (Activity) getContext();
            if (activity != null) {
                view = ((Activity) getContext()).getCurrentFocus();
            }
        }
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(getContext());
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        if(anchor != null) {

            // Get the point to implode to.
            int[] translationXY = getTranslationXY(anchor);
            anchor = null;

            // Collapse the popup back to the anchor.
            container.animate().setDuration(400).translationX(translationXY[0] + paddingStart).translationY(translationXY[1] + paddingTop).scaleX(0).scaleY(0).setListener(new Animator.AnimatorListener() {
                private void onAnimationComplete() {
                    ViewGroup rootView = (ViewGroup) DialogBase.this.getParent();
                    if (rootView != null) {
                        rootView.removeView(DialogBase.this);
                    }
                    container.setScaleX(1);
                    container.setScaleY(1);
                    container.setTranslationX(paddingStart);
                    container.setTranslationY(paddingTop);
                    DialogBase.this.setAlpha(1);

                }

                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    onAnimationComplete();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    onAnimationComplete();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            }).start();

            // Fade everything as well.
            animate().setDuration(400).alpha(0).start();
        }
    }

    /**
     * This is used to initialize the dialog box offsets, width, and height.  By default width
     * and heigh wrap_contents and the offsets are 0.
     *
     * @param width         The width of the container.
     * @param height        The height of the container.
     * @param paddingStart  The start offset to move the container on show.
     * @param paddingTop    The top offset to move the container on show.
     */
    protected void init(float width, float height, float paddingStart, float paddingTop) {
        container.getLayoutParams().width = (int)width;
        container.getLayoutParams().height = (int)height;
        this.paddingStart = paddingStart;
        this.paddingTop = paddingTop;
    }

}
