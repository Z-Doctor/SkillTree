package zdoctor.zskilltree.extra;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.PacketBuffer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SkillTreeProgress {
    private static final SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    private Date obtained;

    public boolean isObtained() {
        return this.obtained != null;
    }

    public void obtain() {
        this.obtained = new Date();
    }

    public void reset() {
        this.obtained = null;
    }

    public Date getObtained() {
        return this.obtained;
    }

    public String toString() {
        return "CriterionProgress{obtained=" + (this.obtained == null ? "false" : this.obtained) + '}';
    }

    public void write(PacketBuffer buf) {
        buf.writeBoolean(this.obtained != null);
        if (this.obtained != null) {
            buf.writeTime(this.obtained);
        }

    }

    public JsonElement serialize() {
        return this.obtained != null ? new JsonPrimitive(DATE_TIME_FORMATTER.format(this.obtained)) : JsonNull.INSTANCE;
    }

    public static SkillTreeProgress read(PacketBuffer buf) {
        SkillTreeProgress skillTreeProgress = new SkillTreeProgress();
        if (buf.readBoolean()) {
            skillTreeProgress.obtained = buf.readTime();
        }

        return skillTreeProgress;
    }

    public static SkillTreeProgress fromJson(String dateTime) {
        SkillTreeProgress skillTreeProgress = new SkillTreeProgress();

        try {
            skillTreeProgress.obtained = DATE_TIME_FORMATTER.parse(dateTime);
            return skillTreeProgress;
        } catch (ParseException parseexception) {
            throw new JsonSyntaxException("Invalid datetime: " + dateTime, parseexception);
        }
    }
}
