package com.pepej.papi.spring;

import com.pepej.papi.plugin.PapiJavaPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;

class SpringBukkitResourceLoader extends DefaultResourceLoader {
    private final List<InternalResourceLoader> resourceLoaders = new ArrayList<>();

    SpringBukkitResourceLoader(PapiJavaPlugin javaPlugin, PluginManager pluginManager) {
        super(getClassLoader(javaPlugin));

        Set<String> dependPluginNames = new HashSet<>();
        dependPluginNames.addAll(javaPlugin.getDescription().getDepend());
        dependPluginNames.addAll(javaPlugin.getDescription().getSoftDepend());

        for (String dependPluginName : dependPluginNames) {
            Plugin dependPlugin = pluginManager.getPlugin(dependPluginName);
            if (dependPlugin instanceof PapiJavaPlugin dependJavaPlugin) {
                ClassLoader classLoader = getClassLoader(dependJavaPlugin);
                InternalResourceLoader resourceLoader = new InternalResourceLoader(classLoader);
                this.resourceLoaders.add(resourceLoader);
            }
        }
    }

    @Override
    public Resource getResource(String location) {
        Resource resourceFromSuper = super.getResource(location);
        if (!resourceFromSuper.exists()) {
            for (ResourceLoader resourceLoader : resourceLoaders) {
                Resource resourceFromDependPlugin = resourceLoader.getResource(location);
                if (resourceFromDependPlugin.exists()) {
                    return resourceFromDependPlugin;
                }
            }
        }
        return resourceFromSuper;
    }

    @Override
    protected Resource getResourceByPath(String path) {
        Resource resourceFromSuper = super.getResourceByPath(path);
        if (!resourceFromSuper.exists()) {
            for (InternalResourceLoader resourceLoader : resourceLoaders) {
                Resource resourceFromDependPlugin = resourceLoader.getResourceByPath(path);
                if (resourceFromDependPlugin.exists()) {
                    return resourceFromDependPlugin;
                }
            }
        }
        return resourceFromSuper;
    }

    private static ClassLoader getClassLoader(PapiJavaPlugin javaPlugin) {
        Method getClassLoaderMethod = ReflectionUtils.findMethod(PapiJavaPlugin.class, "getClassLoader");
        Objects.requireNonNull(getClassLoaderMethod, "A method named 'getClassLoader' not found.");
        ClassLoader classLoader;
        if (!getClassLoaderMethod.canAccess(javaPlugin)) {
            try {
                getClassLoaderMethod.setAccessible(true);
                classLoader = (ClassLoader) ReflectionUtils.invokeMethod(getClassLoaderMethod, javaPlugin);
            } finally {
                getClassLoaderMethod.setAccessible(false);
            }
        } else {
            classLoader = (ClassLoader) ReflectionUtils.invokeMethod(getClassLoaderMethod, javaPlugin);
        }
        return Objects.requireNonNull(classLoader);
    }

    private static class InternalResourceLoader extends DefaultResourceLoader {
        InternalResourceLoader(ClassLoader classLoader) {
            super(classLoader);
        }

        @Override
        public Resource getResourceByPath(String path) {
            return super.getResourceByPath(path);
        }
    }
}