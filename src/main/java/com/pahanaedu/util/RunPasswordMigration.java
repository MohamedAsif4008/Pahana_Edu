package com.pahanaedu.util;

/**
 * Simple runner to execute password migration
 * Run this once to migrate all existing passwords
 */
public class RunPasswordMigration {
    
    public static void main(String[] args) {
        System.out.println("Starting Password Migration...");
        
        try {
            // Run the migration
            PasswordMigrationUtil.migrateAllPasswords();
            
            // Test the migration
            System.out.println("\nTesting migrated passwords...");
            PasswordMigrationUtil.testPasswordVerification();
            
            System.out.println("\nMigration completed successfully!");
            System.out.println("Default passwords:");
            System.out.println("- Admin users: Admin123");
            System.out.println("- Staff users: Staff123");
            
        } catch (Exception e) {
            System.err.println("Migration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
