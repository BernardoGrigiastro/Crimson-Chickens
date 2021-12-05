package crimsonfluff.crimsonchickens.init;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.TranslatableText;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class initConfigs {
    private static initConfigs INSTANCE = null;

    public int MasterSwitchBreeding = 0;
    public int allowBreedingWithVanilla = 80;
    public boolean allowCrossBreeding = true;
    public boolean dropAsBreedingItem = false;
    public int allowDeathDropResource = 80;
    public boolean allowShearingChickens = true;
    public boolean allowConvertingVanilla = true;

    public static void load_config() {
        INSTANCE = new initConfigs();
        Gson gson=new Gson();
        File configFile = new File(FabricLoader.getInstance().getConfigDir().toString(), "crimsonchickens.json");

        try (FileReader reader = new FileReader(configFile)) {
            INSTANCE = gson.fromJson(reader, initConfigs.class);
//            System.out.println("Config: " + INSTANCE);

            try (FileWriter writer = new FileWriter(configFile)) {
                writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(INSTANCE));
//                System.out.println("Config updated!");

            } catch (IOException e2) {
//                System.out.println("Failed to update config file!");
            }

//            System.out.println("Config loaded!");

        } catch (IOException e) {
//            System.out.println("No config found, generating!");
            INSTANCE = new initConfigs();

            try (FileWriter writer = new FileWriter(configFile)) {
                writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(INSTANCE));

            } catch (IOException e2) {
//                System.out.println("Failed to generate config file!");
            }
        }
    }

    public static initConfigs get_instance(){
        if(INSTANCE == null){
            load_config();
//            INSTANCE.parse_colors();
            //INSTANCE.generate_lists();
        }

        return INSTANCE;
    }
}
