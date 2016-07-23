package org.samcrow.antchat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import org.samcrow.antchat.Message.Direction;

/**
 * Displays a single message
 */
public class MessageView extends TextView {
    private static final int PADDING = 20;

    private Message message;

    public MessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTextSize(20);
        setPadding(PADDING, PADDING, PADDING, PADDING);
        setCompoundDrawablePadding(10);
    }

    public MessageView(Context context) {
        this(context, null);
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
        updateMessage();
    }

    private void updateMessage() {
        if (message == null) {
            setText("");
        } else {
            setText(message.getText());

            if (message.getDirection() == Direction.Sent) {
                final Drawable chatSolid = getContext().getResources()
                        .getDrawable(R.drawable.ic_chat_bubble_black_36dp);
                if (chatSolid != null) {
                    chatSolid.setBounds(0, 0, 30, 30);
                    setCompoundDrawables(chatSolid, null, null, null);
                } else {
                    setCompoundDrawables(null, null, null, null);
                }
                // Align left
                setGravity(0x03);
            } else {
                final Drawable chatOutline = getContext().getResources()
                        .getDrawable(R.drawable.ic_chat_bubble_outline_black_36dp);
                if (chatOutline != null) {
                    chatOutline.setBounds(0, 0, 30, 30);
                    setCompoundDrawables(null, null, chatOutline, null);
                } else {
                    setCompoundDrawables(null, null, null, null);
                }
                // Align right
                setGravity(0x05);
            }
        }
    }
}
