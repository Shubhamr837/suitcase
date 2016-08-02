package net;

import ui.ProgressBarStatus;

import javax.swing.*;
import java.beans.ExceptionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public abstract class SuitcaseSwingWorker<T> extends SwingWorker<T, ProgressBarStatus> {
  public static final String STRING_PROPERTY = "string";
  public static final String INDETERMINATE_PROPERTY = "indeterminate";
  public static final String PROGRESS_PROPERTY = "progress"; // hardcoded in SwingWorker
  public static final String DONE_PROPERTY = "done";

  private static final int BLOCKING_EXEC_WAIT = 500;

  protected T result;
  private boolean isDone;

  public SuitcaseSwingWorker() {
    this.result = null;
    this.isDone = false;
  }

  @Override
  protected final void process(List<ProgressBarStatus> chunks) {
    ProgressBarStatus latestStatus = chunks.get(chunks.size() - 1);

    setStatus(latestStatus);
  }

  /**
   * Calls abstract method finished(), which is to be implement by implementations of this class.
   * The finished() method should contain what is usually in the done() method.
   */
  @Override
  protected final void done() {
    try {
      finished();
      isDone = true;
    } finally {
      notifyDone();
    }
  }

  /**
   * Called by done()
   * Should contain what's usually in done()
   */
  protected abstract void finished();

  protected void setStatus(ProgressBarStatus status) {
    if (status.getProgress() != null) {
      setProgress(status.getProgress());
    }
    setString(status.getString());
    setIndeterminate(status.isIndeterminate());
  }

  protected void setString(String string) {
    firePropertyChange(STRING_PROPERTY, null, string);
  }

  protected void setIndeterminate(Boolean indeterminate) {
    firePropertyChange(INDETERMINATE_PROPERTY, null, indeterminate);
  }

  private void notifyDone() {
    firePropertyChange(DONE_PROPERTY, null, true);
  }

  public T blockingExecute() {
    this.execute();

    while (!isDone) {
      try {
        Thread.sleep(BLOCKING_EXEC_WAIT);
      } catch (Exception e) {/* ignored */}
    }

    return result;
  }
}
