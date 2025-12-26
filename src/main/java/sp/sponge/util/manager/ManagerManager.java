package sp.sponge.util.manager;

import org.jetbrains.annotations.NotNull;
import sp.sponge.Sponge;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ManagerManager {
    private static final List<Manager> managerList = new ArrayList<>();

    public static <T extends Manager> T register(T manager) {
        managerList.add(manager);

        return manager;
    }

    public static void initAssets() {
        for (Manager manager : managerList) {
            visitFiles(manager.getDirectoryToCheck(), manager::acceptPath);
        }
    }

    private static void visitFiles(String directoryToCheck, BiConsumer<Path, Path> operation) {
        Path path = Sponge.getAssetFile("").toPath().resolve(directoryToCheck);
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @NotNull
                @Override
                public FileVisitResult visitFile(@NotNull Path file, @NotNull BasicFileAttributes attrs) throws IOException {
                    operation.accept(path.relativize(file), file.toAbsolutePath());
                    return super.visitFile(file, attrs);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
