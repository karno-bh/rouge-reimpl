package il.ac.sce.ir.metric.starter.gui.main.event.component_event;

import il.ac.sce.ir.metric.starter.gui.main.util.pubsub.Event;

public class FileChoosePanelEvent implements Event {

    private final String source;

    private final String fileName;

    public FileChoosePanelEvent(String source, String fileName) {
        this.source = source;
        this.fileName = fileName;
    }

    public String getSource() {
        return source;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("{ 'eventName': 'FileChoosePanelEvent', ")
                .append("'source': '").append(source).append("', ")
                .append("'fileName': '").append(fileName).append("' }");
        return sb.toString();
    }
}
