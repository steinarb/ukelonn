import { defineConfig } from 'eslint/config';
import react from 'eslint-plugin-react';

export default defineConfig([
	{
        files: ['**/*.js'],
        languageOptions: { parserOptions: { ecmaFeatures: { jsx: true } } },
        plugins: { react },
		rules: {
            ...react.configs.recommended.rules,
            'react/prop-types': 'off',
        },
    },
]);
