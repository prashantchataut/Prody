import { sveltekit } from '@sveltejs/kit/vite';
import { defineConfig } from 'vitest/config';

export default defineConfig({
	plugins: [sveltekit()],
	test: {
		environment: 'jsdom',
		include: ['tests/unit/**/*.test.ts'],
		coverage: {
			provider: 'v8',
			reporter: ['text', 'html'],
			exclude: ['node_modules', '.svelte-kit', 'tests/e2e']
		},
		setupFiles: ['tests/setup.ts']
	},
	optimizeDeps: {
		exclude: ['@xenova/transformers']
	}
});
