package com.collabedit.collabserver;

import java.util.Stack;

public class UndoRedoManager {

    private final Stack<EditOperation> undoStack;
    private final Stack<EditOperation> redoStack;
    private final int Maxsize = 3;

    // Constructor: initialize both stacks
    public UndoRedoManager() {
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
    }

    // Record a new operation: push onto undo stack, trim if over Maxsize, clear redo
    public void recordOperation(EditOperation op) {
        if (undoStack.size() >= Maxsize) {
            undoStack.remove(0);  // remove the oldest entry when over capacity
        }
        undoStack.push(op);
        redoStack.clear();        // any new action invalidates the redo history
    }

    // Undo: pop from undo, push into redo, return the opposite operation to apply
    public EditOperation undo() {
        if (!undoStack.isEmpty()) {
            EditOperation lastOp = undoStack.pop();
            EditOperation opposite = createOppositeOp(lastOp);
            redoStack.push(lastOp);
            return opposite;
        }
        return null;  // nothing to undo
    }

    // Redo: pop from redo, push back into undo, return that operation to reapply
    public EditOperation redo() {
        if (!redoStack.isEmpty()) {
            EditOperation lastUndone = redoStack.pop();
            undoStack.push(lastUndone);
            return lastUndone;
        }
        return null;  // nothing to redo
    }

    // Build and return the opposite of a given operation
    private EditOperation createOppositeOp(EditOperation op) {
        EditOperation opposite = new EditOperation();

        if ("insert".equals(op.op)) {
            // Undoing an insert means deleting at the same position/ID
            opposite.op = "delete";
            opposite.clock = op.clock;
            opposite.uid = op.uid;
            opposite.parent = op.parent;
            opposite.id = op.id;
            // value not needed on delete
        } else if ("delete".equals(op.op)) {
            // Undoing a delete means inserting the same value back
            opposite.op = "insert";
            opposite.clock = op.clock;
            opposite.uid = op.uid;
            opposite.value = op.value;
            opposite.parent = op.parent;
            opposite.id = op.id;
        }

        return opposite;
    }

    // NEW: Print out the current contents of undo and redo stacks
    public void showStacks() {
        System.out.println("\n--- Current Undo/Redo Stacks ---");
        System.out.print("Undo Stack (bottom→top): ");
        if (undoStack.isEmpty()) {
            System.out.print("[empty]");
        } else {
            // print from bottom (0) to top (size-1)
            for (int i = 0; i < undoStack.size(); i++) {
                EditOperation e = undoStack.get(i);
                System.out.print(e.value + (i < undoStack.size() - 1 ? ", " : ""));
            }
        }

        System.out.print("\nRedo Stack (bottom→top): ");
        if (redoStack.isEmpty()) {
            System.out.print("[empty]");
        } else {
            for (int i = 0; i < redoStack.size(); i++) {
                EditOperation e = redoStack.get(i);
                System.out.print(e.value + (i < redoStack.size() - 1 ? ", " : ""));
            }
        }
        System.out.println("\n---------------------------------\n");
    }
    // Clears both undo and redo stacks (for use after file import)
    public void clearStacks() {
        undoStack.clear();
        redoStack.clear();
    }

}
