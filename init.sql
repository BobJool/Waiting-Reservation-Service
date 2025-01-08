-- Check and create 'auth_db'
SELECT 'CREATE DATABASE auth_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'auth_db')
\gexec

-- Check and create 'notification_db'
SELECT 'CREATE DATABASE notification_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'notification_db')
\gexec

-- Check and create 'payment_db'
SELECT 'CREATE DATABASE payment_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'payment_db')
\gexec

-- Check and create 'queue_db'
SELECT 'CREATE DATABASE queue_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'queue_db')
\gexec

-- Check and create 'reservation_db'
SELECT 'CREATE DATABASE reservation_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'reservation_db')
\gexec

-- Check and create 'restaurant_db'
SELECT 'CREATE DATABASE restaurant_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'restaurant_db')
\gexec
