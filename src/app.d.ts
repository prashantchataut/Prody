import type { SupabaseClient, Session, User } from '@supabase/supabase-js';

// Database type — placeholder until Supabase generates it
// Will be replaced in the database session
type Database = Record<string, unknown>;

declare global {
	namespace App {
		interface Locals {
			supabase: SupabaseClient<Database>;
			safeGetSession: () => Promise<{
				session: Session | null;
				user: User | null;
			}>;
		}

		interface PageData {
			session: Session | null;
			user: User | null;
		}

		interface Error {
			message: string;
			code: string;
		}
	}
}

export {};
