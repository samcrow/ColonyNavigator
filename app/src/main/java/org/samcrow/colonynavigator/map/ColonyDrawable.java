package org.samcrow.colonynavigator.map;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;

import org.samcrow.colonynavigator.data4.Colony;

public class ColonyDrawable extends Drawable {

    /**
     * Background shape alpha (transparency), 0-255
     */
    private static final int BG_ALPHA = 100;
    /**
     * Background color for non-focus, non-visited colonies
     */
    private static final int BG_NORMAL_COLOR = Color.argb(BG_ALPHA / 2, 100, 100, 100); // gray
    /**
     * Background color for focus colonies
     */
    private static final int BG_FOCUS_COLOR = Color.argb(BG_ALPHA, 115, 140, 255); // blue
    /**
     * Background color for visited colonies, both focus and non-focus
     */
    private static final int BG_VISITED_COLOR = Color.argb(BG_ALPHA, 77, 240, 101); // green

    /**
     * Background circle radius
     */
    private static final int BG_RADIUS = 40;

    /**
     * Text size (height?) in pixels
     */
    private static final float LABEL_TEXT_SIZE = 30;

    /**
     * Colony location point color
     */
    private static final int POINT_COLOR = Color.BLACK;
    /**
     * Colony number label color
     */
    private static final int LABEL_COLOR = Color.BLACK;
    /**
     * Colony location circle radius
     */
    private static final int POINT_RADIUS = 6;
    /**
     * Line color for the circle drawn around the selected colony
     */
    private static final int SELECTED_CIRCLE_COLOR = Color.RED;
    /**
     * Line width for the circle drawn around the selected colony
     */
    private static final int SELECTED_CIRCLE_LINE_WIDTH = 12;

    private static final float SELECTED_CIRCLE_RADIUS = BG_RADIUS - (SELECTED_CIRCLE_LINE_WIDTH / 2f);

    /**
     * The horizontal distance from the center that the colony number text is offset
     */
    private static final int TEXT_X_OFFSET = 10;

    private final Paint paint;
    private final Colony colony;
    private final int idStringWidth;
    private final FontMetrics metrics;
    private boolean colonySelected;

    public ColonyDrawable(Colony colony) {
        this.paint = new Paint();
        // Make text larger
        this.paint.setTextSize(LABEL_TEXT_SIZE);

        this.colony = colony;
        String colonyIdString = colony.getID();
        idStringWidth = (int) Math.ceil(paint.measureText(colonyIdString));

        paint.setAntiAlias(true);
        metrics = paint.getFontMetrics();

        //Create a bounding box
        int left = -BG_RADIUS;
        int right = BG_RADIUS + idStringWidth;
        int top = -BG_RADIUS;
        int bottom = BG_RADIUS;
        setBounds(left, top, right, bottom);

    }

    /**
     * @return The distance that this drawable
     * should be moved right along the X axis
     * to make the center of its point match up
     * with the center of the dimensions of this drawable
     */
    public int getXOffset() {
        if (idStringWidth + TEXT_X_OFFSET <= BG_RADIUS) {
            return 0;
        } else {
            return (idStringWidth + TEXT_X_OFFSET) - BG_RADIUS;
        }
    }


    @Override
    public int getIntrinsicHeight() {
        return 2 * BG_RADIUS;
    }

    @Override
    public int getIntrinsicWidth() {
        if (idStringWidth + TEXT_X_OFFSET > BG_RADIUS) {
            return BG_RADIUS + idStringWidth + TEXT_X_OFFSET;
        } else {
            return 2 * BG_RADIUS;
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        //Paint within bounds
        //Calculate the X/Y canvas coordinate position of the actual colony position
        Rect bounds = getBounds();
        float centerX = bounds.left + BG_RADIUS;
        float centerY = bounds.top + BG_RADIUS;

        //Big background circle
        int backgroundColor;
        if (colony.getAttribute("census.visited", false)) {
            backgroundColor = BG_VISITED_COLOR;
        } else {
            if (colony.getAttribute("census.focus", false)) {
                backgroundColor = BG_FOCUS_COLOR;
            } else {
                backgroundColor = BG_NORMAL_COLOR;
            }
        }
        //Set paint to fill only, with the required color
        paint.setColor(backgroundColor);
        paint.setStyle(Style.FILL);

        if (colony.getAttribute("census.visited", false)) {
            if (colony.getAttribute("census.focus", false)) {
                // Visited, focus colony
                // Draw a two-part circle
                drawTwoColorCircle(canvas, centerX, centerY, BG_VISITED_COLOR, BG_FOCUS_COLOR);
            } else {
                // Visited, not focus colony
                drawBackgroundCircle(canvas, centerX, centerY, BG_VISITED_COLOR);
            }
        } else {
            if (colony.getAttribute("census.focus", false)) {
                // Not visited, focus colony
                drawBackgroundCircle(canvas, centerX, centerY, BG_FOCUS_COLOR);

            } else {
                // Not visited, not focus colony
                drawBackgroundCircle(canvas, centerX, centerY, BG_NORMAL_COLOR);
            }
        }


        // Draw the circle around the colony if it is selected
        if (colonySelected) {
            paint.setStyle(Style.STROKE);
            paint.setColor(SELECTED_CIRCLE_COLOR);
            paint.setStrokeWidth(SELECTED_CIRCLE_LINE_WIDTH);

            canvas.drawCircle(centerX, centerY, SELECTED_CIRCLE_RADIUS, paint);

        }

        //Draw colony location point
        paint.setStyle(Style.FILL);
        paint.setColor(POINT_COLOR);
        canvas.drawCircle(centerX, centerY, POINT_RADIUS, paint);

        //Draw colony number
        paint.setColor(LABEL_COLOR);
        canvas.drawText(String.valueOf(colony.getID()), centerX + TEXT_X_OFFSET,
                centerY + metrics.descent, paint);
    }

    /**
     * Draws a background circle in the provided color
     */
    private void drawBackgroundCircle(Canvas canvas, float centerX, float centerY, int color) {
        paint.setColor(color);
        paint.setStyle(Style.FILL);

        canvas.drawCircle(centerX, centerY, BG_RADIUS, paint);
    }

    private void drawTwoColorCircle(Canvas canvas, float centerX, float centerY, int leftColor,
                                    int rightColor) {
        final RectF rect = new RectF(centerX - BG_RADIUS, centerY - BG_RADIUS, centerX + BG_RADIUS,
                centerY + BG_RADIUS);
        paint.setStyle(Style.FILL);
        // Draw left arc
        paint.setColor(leftColor);
        canvas.drawArc(rect, 90, 180, true, paint);
        // Draw right arc
        paint.setColor(rightColor);
        canvas.drawArc(rect, -90, 180, true, paint);
    }

    public boolean isColonySelected() {
        return colonySelected;
    }

    public void setColonySelected(boolean colonySelected) {
        this.colonySelected = colonySelected;
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
        //ignore

    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        //ignoring

    }

}
